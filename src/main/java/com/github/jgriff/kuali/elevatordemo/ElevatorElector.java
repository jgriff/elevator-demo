package com.github.jgriff.kuali.elevatordemo;

import com.github.jgriff.kuali.elevatordemo.events.*;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * Bean responsible for electing an elevator to respond to floor requests/calls.  These
 * come in the form of an {@link ElevatorRequestUpEvent} or {@link ElevatorRequestDownEvent}.
 * 
 * This bean will consult the {@link ElevatorRegistry} for the available elevators, and choose
 * the one best suited to answer the call.  An answer is given in the way of publishing an
 * {@link ElevatorConfirmEvent}, which signals back to the requesting floor that an elevator
 * is on the way (or if the request cannot be serviced).
 * 
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
@Service
@Setter
public class ElevatorElector {
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private List<Elevator> elevators;
    private Map<UUID, ElevatorStatus> latestElevatorStatuses = Collections.synchronizedMap(new HashMap<>());

    /**
     * Monitors and caches latest status updates from the elevators.  This elector uses these latest
     * statuses to quickly decide which elevator is best suited to answer a call from a floor.  By 
     * monitoring these events, this elector does need to query each and every elevator for status
     * before it can make a decision.
     */
    @EventListener
    public void onElevatorStatusUpdate(@NonNull ElevatorStatusUpdateEvent elevatorEvent) {
        Mono.just(elevatorEvent)
                .map(ElevatorStatusUpdateEvent::getStatusUpdate)
                .subscribe((s) -> latestElevatorStatuses.put(s.getDescriptor().getId(), s));
    }

    @EventListener
    public void onElevatorRequestUp(ElevatorRequestUpEvent request) {
        Mono.just(request)
            .map(ElevatorRequestEvent::getRequestFloor)
            // TODO stoppedAt 
            // TODO passingBy (going UP)
            .flatMap(this::elevatorClosestTo)
            .flatMap(id -> Flux.fromIterable(elevators)
                .filter(e -> e.describe().getId().equals(id))
                .take(1)
                .single())
            .doOnNext(e -> e.moveToFloor(request.getRequestFloor()))
            .doOnNext(e -> eventPublisher.publishEvent(ElevatorConfirmEvent.confirming(request)))
            .doOnError(e -> eventPublisher.publishEvent(ElevatorConfirmEvent.errorFor(request)))
            .subscribe();
    }

    @EventListener
    public void onElevatorRequestDown(ElevatorRequestDownEvent request) {
        Mono.just(request)
            .map(ElevatorRequestEvent::getRequestFloor)
            // TODO stoppedAt 
            // TODO passingBy (going DOWN)
            .flatMap(this::elevatorClosestTo)
            .flatMap(id -> Flux.fromIterable(elevators)
                .filter(e -> e.describe().getId().equals(id))
                .take(1)
                .single())
            .doOnNext(e -> e.moveToFloor(request.getRequestFloor()))
            .doOnNext(e -> eventPublisher.publishEvent(ElevatorConfirmEvent.confirming(request)))
            .doOnError(e -> eventPublisher.publishEvent(ElevatorConfirmEvent.errorFor(request)))
            .subscribe();
    }
    

    private Mono<UUID> elevatorStoppedAt(int floor) {
        // TODO choose an elevator stopped at this floor, or empty if there is none
        return Mono.empty();
    }

    private Mono<UUID> elevatorPassingBy(int floor, ElevatorMovingEvent.Direction direction) {
        // TODO choose an elevator passing by this floor, or null if there is none
        return Mono.empty();
    }

    /**
     * Determine the elevator currently closest to a specified floor.  This is the fallback
     * election method if no other more efficient selection can be made.
     * 
     * @param floor the floor to find the closest elevator to.
     * @return closest elevator, or {@code null} if there are none available
     * @see #elevatorStoppedAt(int)
     * @see #elevatorPassingBy(int, ElevatorMovingEvent.Direction)  
     * @since 0.0.0
     */
    private Mono<UUID> elevatorClosestTo(int floor) {
        ElevatorStatus winner = null;
        int smallestSoFar = Integer.MAX_VALUE;
        for (ElevatorStatus candidate : latestElevatorStatuses.values()) {
            int candidateDiff = Math.abs(candidate.getCurrentFloor() - floor);
            if (winner == null || candidateDiff < smallestSoFar) {
                winner = candidate;
                smallestSoFar = candidateDiff;
            }
        }
        
        return Optional.ofNullable(winner)
                .map(ElevatorStatus::getDescriptor)
                .map(ElevatorDescriptor::getId)
                .map(Mono::just)
                .orElse(Mono.empty());        
    }
}
