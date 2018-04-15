package com.github.jgriff.kuali.elevatordemo;

import com.github.jgriff.kuali.elevatordemo.events.*;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
@Builder
@Getter
@Slf4j
public class BasicElevator implements Elevator {
    private final UUID id = UUID.randomUUID();
    @Builder.Default
    private String name = "Basic Elevator";
    
    @Builder.Default 
    private final int firstFloor = 1, topFloor = 10;
    @Builder.Default
    private int currentFloor = 1;
    @Builder.Default
    private List<Integer> floorsToStopAt = Collections.synchronizedList(new ArrayList<>());
    private Integer lastScheduledFloor;

    /**
     * Speed at which this elevator can move between floors.
     */
    @Builder.Default
    private final Duration movementSpeed = Duration.ofSeconds(3);
    /**
     * How long the doors remain open (before closing) at a floor.
     */
    @Builder.Default
    private final Duration doorOpenDelay = Duration.ofSeconds(5);
    
    private Disposable floorVisitations;
    private FluxSink<Integer> floorVisitScheduler;
    private ApplicationEventPublisher eventPublisher;
    
    private int floorsPassed, tripsMade;

    @Override
    public ElevatorDescriptor describe() {
        return ElevatorDescriptor.builder()
                .id(getId())
                .name(getName())
                .build();
    }

    @Override
    public void moveToFloor(int floor) throws InvalidElevatorRequestException {
        if (floor < getFirstFloor()) throw new InvalidElevatorRequestException("Cannot go to floor '" + floor + "', it is below the first floor (" + getFirstFloor() + ").");
        if (floor > getTopFloor()) throw new InvalidElevatorRequestException("Cannot go to floor '" + floor + "', it is above the top floor (" + getTopFloor() + ").");
        
        floorsToStopAt.add(floor);
        if (floorVisitations == null) {
            floorVisitations = createFloorVisitFlux();
        }
        
        floorVisitScheduler.next(floor);
    }

    /**
     * Factory method for creating our {@link Flux} that simulates the movement of this elevator.  
     * Used whenever the elevator goes into service initially, or back into service (out of maintenance).
     * 
     * @return the configured flux simulating elevator movement.
     * @since 0.0.0
     */
    private Disposable createFloorVisitFlux() {
        return Flux.create((FluxSink<Integer> emitter) -> this.floorVisitScheduler = emitter)
                .distinct() // don't schedule a duplicate stop at a floor we are already scheduled to stop at
                .doOnNext((f) -> log.debug("Floor '" + f + "' has been requested and queued for elevator: " + describe()))
              
                .publishOn(Schedulers.elastic())
                .delayElements(getMovementSpeed())
                
                // before we visit the next floor, schedule the interim floors we must pass
                .flatMapSequential(this::interimFloors)  
                
                // move to the floor
                .flatMapSequential(this::movingToFloor)
                
                // also include diagnostic update whenever we stop at a floor
                .flatMapSequential(e -> {
                    if (e instanceof ElevatorFloorStopEvent) {
                        return Flux.just(e, ElevatorDiagnosticUpdateEvent.forDiagnostic(currentDiagnostic()));
                    } else {
                        return Flux.just(e);
                    }
                })
                
                .subscribe(eventPublisher::publishEvent);
    }

    private Flux<Integer> interimFloors(int targetFloor) {
        if (lastScheduledFloor == null) {
            lastScheduledFloor = getCurrentFloor();
        }
        List<Integer> floorsBetween = floorsBetween(lastScheduledFloor, targetFloor).collect(Collectors.toList());
        lastScheduledFloor = floorsBetween.stream().reduce((a, b) -> b).orElse(null);
        return Flux.fromIterable(floorsBetween);
    }

    private Stream<Integer> floorsBetween(int start, int finish) {
        if (start == finish) return Stream.empty();    

        if (start < finish) {
            // going up...
            return Stream.iterate(start + 1, (f) -> ++f).limit(finish - start);
        } else {
            // going down...
            return Stream.iterate(start - 1, (f) -> --f).limit(start - finish);
        }
    }
    
    private Flux<ElevatorOperationEvent> movingToFloor(int floor) {
        return Mono.just(floor)
                // we're moving...
                .doOnNext((f) -> eventPublisher.publishEvent(
                        ElevatorMovingEvent.builder()
                            .elevator(describe())
                            .direction(currentFloor < f ? ElevatorMovingEvent.Direction.UP : ElevatorMovingEvent.Direction.DOWN)
                            .nextFloor(f)
                            .build()
                ))
                
                .doOnNext((newCurrentFloor) -> currentFloor = newCurrentFloor)
                
                // are we stopping here?
                .map(floorsToStopAt::remove)
                .doOnNext(f -> publishCurrentStatus())
                .flatMapMany((stopping) -> stopping ? stoppingAtFloor(floor) : passingByFloor(floor));
    }

    private Flux<ElevatorOperationEvent> stoppingAtFloor(int floor) {
        ++floorsPassed;
        ++tripsMade;
        return Flux.just(
                ElevatorFloorStopEvent.builder()
                        .elevator(describe())
                        .floor(floor)
                        .build(),
                ElevatorDoorOpenEvent.builder()
                        .elevator(describe())
                        .floor(floor)
                        .build())
                .cast(ElevatorOperationEvent.class)

                // delay the door close event to allow the door to stay open for a bit
                .concatWith(Flux.just(
                        ElevatorDoorCloseEvent.builder()
                                .elevator(describe())
                                .floor(floor)
                                .build())
                        .publishOn(Schedulers.elastic())
                        .delayElements(getDoorOpenDelay()));        
    }  

    private Flux<ElevatorOperationEvent> passingByFloor(int floor) { 
        ++floorsPassed;
        return Flux.just(
                ElevatorFloorPassEvent.builder()
                        .elevator(describe())
                        .floor(floor)
                        .build());        
    }
    
    private void publishCurrentStatus() {
        eventPublisher.publishEvent(ElevatorStatusUpdateEvent.forStatus(currentStatus()));
    }
    
    private ElevatorStatus currentStatus() {
        return ElevatorStatus.builder()
                .descriptor(describe())
                .operationalState(ElevatorOperationalState.OPERATIONAL) // TODO monitor activity and put into maintenance when threshold exceeded
                .currentFloor(getCurrentFloor())
                .futureStops(new ArrayList<>(getFloorsToStopAt()))
                .build();
    }
    
    private void publishCurrentDiagnostic() {
        eventPublisher.publishEvent(ElevatorDiagnosticUpdateEvent.forDiagnostic(currentDiagnostic()));
    }
    
    private ElevatorDiagnostic currentDiagnostic() {
        return ElevatorDiagnostic.builder()
                .descriptor(describe())
                .floorsPassed(getFloorsPassed())
                .tripsMade(getTripsMade())
                .build();
    }
}
