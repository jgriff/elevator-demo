package com.github.jgriff.kuali.elevatordemo;

import com.github.jgriff.kuali.elevatordemo.events.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
public class ElevatorTests implements MockitoTest, EventTest {
    @Mock 
    private ApplicationEventPublisher eventPublisher;
    private ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
    
    @InjectMocks 
    private BasicElevator sut = BasicElevator.builder()
            .topFloor(10)
            // change all timing delays to instantly, for test purposes
            .movementSpeed(Duration.ZERO)
            .doorOpenDelay(Duration.ZERO)
            .build();
    
    @Test
    void startsOnFirstFloor() {
        assertEquals(1, sut.getCurrentFloor());
    }           
    
    @Test
    @DisplayName("moving to a floor produces a 'ElevatorMovingEvent' when the elevator begins to move")
    void moveToAFloorProducesMovingEvent() {
        // when: a request is made to move to the next floor
        sut.moveToFloor(2);
        
        // then: the elevator publishes a move event when it starts moving
        ElevatorMovingEvent expectedEvent = captureEvent(ElevatorMovingEvent.class);
        
        // and: the event indicates it is moving UP and it is our elevator
        assertEquals(ElevatorMovingEvent.Direction.UP, expectedEvent.getDirection(), "Moving event did not declare correct direction.");
        assertEquals(sut.describe().getId(), expectedEvent.getElevator().getId(), "Moving event did not declare correct elevator.");
        assertEquals(2, expectedEvent.getNextFloor(), "Moving event did not declare next floor.");
    }
    
    @Test
    @DisplayName("moving to a floor produces a 'ElevatorFloorStopEvent' when the elevator arrives")
    void moveToAFloorProducesStopEventAtDestination() {
        // when: a request is made to move to the next floor
        sut.moveToFloor(2);
        
        // then: the elevator publishes a stop event when it arrives
        ElevatorFloorStopEvent expectedEvent = captureEvent(ElevatorFloorStopEvent.class);
        
        // and: the event indicates it is for the floor we moved to and it is our elevator
        assertEquals(2, expectedEvent.getFloor(), "Visit event did not declare correct floor.");
        assertEquals(sut.describe().getId(), expectedEvent.getElevator().getId(), "Visit event did not declare correct elevator.");
    }
    
    @Test
    @DisplayName("stopping at a floor produces a 'ElevatorDoorOpenEvent' after the elevator stops")
    void stoppingAtFloorProducesDoorOpenEventAtDestination() {
        // when: a request is made to move to a floor
        sut.moveToFloor(2);
        
        // then: the elevator publishes a door open event after it arrives
        ElevatorDoorOpenEvent expectedEvent = captureEvent(ElevatorDoorOpenEvent.class);
        
        // and: the event indicates it is for the floor we moved to and it is our elevator
        assertEquals(2, expectedEvent.getFloor(), "Visit event did not declare correct floor.");
        assertEquals(sut.describe().getId(), expectedEvent.getElevator().getId(), "Visit event did not declare correct elevator.");
    }
    
    @Test
    @DisplayName("stopping at a floor produces a 'ElevatorDoorCloseEvent' after the elevator closes its doors")
    void stoppingAtFloorProducesDoorCloseEventAtDestination() {
        // when: a request is made to move to a floor
        sut.moveToFloor(2);
        
        // then: the elevator publishes a door close event after it arrives
        ElevatorDoorCloseEvent expectedEvent = captureEvent(ElevatorDoorCloseEvent.class);
        
        // and: the event indicates it is for the floor we moved to and it is our elevator
        assertEquals(2, expectedEvent.getFloor(), "Visit event did not declare correct floor.");
        assertEquals(sut.describe().getId(), expectedEvent.getElevator().getId(), "Visit event did not declare correct elevator.");
    }
    
    @Test
    @DisplayName("stopping at a floor produces 'stop/door open/door close' events in correct order")
    void stoppingAtFloorProducesStopOpenCloseEventsInCorrectOrder() {
        // when: a request is made to move to a floor
        sut.moveToFloor(2);
        
        // then: the elevator publishes its events
        List<ElevatorOperationEvent> expectedEvents = captureEvents((e) -> Arrays.asList(
                    ElevatorFloorStopEvent.class, 
                    ElevatorDoorOpenEvent.class, 
                    ElevatorDoorCloseEvent.class).stream()
                
                .anyMatch((it) -> it.isAssignableFrom(e.getClass())), 3);
        
        // and: the events are in the correct order
        assertTrue(expectedEvents.get(0) instanceof ElevatorFloorStopEvent);
        assertTrue(expectedEvents.get(1) instanceof ElevatorDoorOpenEvent);
        assertTrue(expectedEvents.get(2) instanceof ElevatorDoorCloseEvent);
    }
    
    @Test
    @DisplayName("passing a floor (not stopped at) produces a 'ElevatorFloorPassEvent'")
    void movingToAFloorProducesPassEventForInterimFloors() {  
        // when: a request is made to move to a floor
        sut.moveToFloor(5);
        
        // then: the elevator publishes 'pass' events for floors 2/3/4 and 'stop' event for floor 5
        List<ElevatorOperationEvent> expectedEvents = captureEvents((e) -> Arrays.asList(
                    ElevatorFloorPassEvent.class,
                    ElevatorFloorStopEvent.class).stream()
                
                .anyMatch((it) -> it.isAssignableFrom(e.getClass())), 4);         
        
        // and: the events are in the correct order
        assertTrue(expectedEvents.get(0) instanceof ElevatorFloorPassEvent);
        assertEquals(2, ((ElevatorFloorPassEvent)expectedEvents.get(0)).getFloor());
        assertTrue(expectedEvents.get(1) instanceof ElevatorFloorPassEvent);
        assertEquals(3, ((ElevatorFloorPassEvent)expectedEvents.get(1)).getFloor());
        assertTrue(expectedEvents.get(2) instanceof ElevatorFloorPassEvent);
        assertEquals(4, ((ElevatorFloorPassEvent)expectedEvents.get(2)).getFloor());
        assertTrue(expectedEvents.get(3) instanceof ElevatorFloorStopEvent);
        assertEquals(5, ((ElevatorFloorStopEvent)expectedEvents.get(3)).getFloor());
    }   
    
    @Test
    @DisplayName("moving to a floor produces a 'ElevatorStatusUpdateEvent' for each floor it passes")
    void moveToAFloorProducesStatusUpdateEventForEachFloorPassed() {
        // when: a request is made to move to another floor
        sut.moveToFloor(5);
        
        // then: the elevator publishes a status event at each floor it visits
        List<ElevatorStatusUpdateEvent> expectedEvents = captureEvents(
            it -> ElevatorStatusUpdateEvent.class.isAssignableFrom(it.getClass()), 4);         
        
        // and: the events are in the correct order
        assertEquals(2, expectedEvents.get(0).getStatusUpdate().getCurrentFloor().intValue());
        assertTrue(expectedEvents.get(0).getStatusUpdate().getFutureStops().contains(5), "Should declare all future stops.");
        assertEquals(3, expectedEvents.get(1).getStatusUpdate().getCurrentFloor().intValue());
        assertTrue(expectedEvents.get(1).getStatusUpdate().getFutureStops().contains(5), "Should declare all future stops.");
        assertEquals(4, expectedEvents.get(2).getStatusUpdate().getCurrentFloor().intValue());
        assertTrue(expectedEvents.get(2).getStatusUpdate().getFutureStops().contains(5), "Should declare all future stops.");
        assertEquals(5, expectedEvents.get(3).getStatusUpdate().getCurrentFloor().intValue());
        assertTrue(expectedEvents.get(3).getStatusUpdate().getFutureStops().isEmpty(), "Once the last stop is arrived at, should not have any future stops.");
    }
    
    @Test
    @DisplayName("publishes a 'ElevatorDiagnosticUpdateEvent' whenever it stops at a floor")
    void publishesDiagnosticsWheneverItStops() {
        // when: a request is made to move to another floor
        sut.moveToFloor(5);
        
        // then: the elevator publishes a diagnostic event when it stops at the floor
        List<Object> expectedEvents = captureEvents((e) -> Arrays.asList(
                    ElevatorFloorStopEvent.class,
                    ElevatorDiagnosticUpdateEvent.class).stream()
                
                .anyMatch((it) -> it.isAssignableFrom(e.getClass())), 2);         
        
        // and: the events are in the correct order
        assertTrue(expectedEvents.get(0) instanceof ElevatorFloorStopEvent);
        assertTrue(expectedEvents.get(1) instanceof ElevatorDiagnosticUpdateEvent);
        
        // and: the diagnostic has our expected values
        ElevatorDiagnostic diagnostic = ((ElevatorDiagnosticUpdateEvent)expectedEvents.get(1)).getDiagnosticUpdate();
        assertEquals(sut.getId(), diagnostic.getDescriptor().getId(), "Diagnostic should carry the ID of the elevator.");
        assertEquals(4, diagnostic.getFloorsPassed().intValue(), "Should have passed 4 floors.");
        assertEquals(1, diagnostic.getTripsMade().intValue(), "Should have made 1 trip.");
    }
    
    @Test
    @DisplayName("elevator moves down in same manner")
    void canMoveDownFloors() {
        Elevator sut = BasicElevator.builder()
                .currentFloor(5)
                .eventPublisher(eventPublisher)
                .movementSpeed(Duration.ZERO) // move instantly, for test purposes
                .build();
        
        // when: a request is made to move to a lower floor
        sut.moveToFloor(1);
        
        // then: the elevator publishes 'pass' events for floors 4/3/2 and 'stop' event for floor 1
        List<ElevatorOperationEvent> expectedEvents = captureEvents((e) -> Arrays.asList(
                    ElevatorFloorPassEvent.class,
                    ElevatorFloorStopEvent.class).stream()
                
                .anyMatch((it) -> it.isAssignableFrom(e.getClass())), 4);         
        
        // and: the events are in the correct order
        assertTrue(expectedEvents.get(0) instanceof ElevatorFloorPassEvent);
        assertEquals(4, ((ElevatorFloorPassEvent)expectedEvents.get(0)).getFloor());
        assertTrue(expectedEvents.get(1) instanceof ElevatorFloorPassEvent);
        assertEquals(3, ((ElevatorFloorPassEvent)expectedEvents.get(1)).getFloor());
        assertTrue(expectedEvents.get(2) instanceof ElevatorFloorPassEvent);
        assertEquals(2, ((ElevatorFloorPassEvent)expectedEvents.get(2)).getFloor());
        assertTrue(expectedEvents.get(3) instanceof ElevatorFloorStopEvent);
        assertEquals(1, ((ElevatorFloorStopEvent)expectedEvents.get(3)).getFloor());
    }
    
    @Test
    @DisplayName("requesting floor below first floor raises an 'InvalidElevatorRequestException'")
    void requestFloorBelowFirstRaisesException() {
        InvalidElevatorRequestException thrown = assertThrows(InvalidElevatorRequestException.class, () -> sut.moveToFloor(0));

        assertEquals("Cannot go to floor '0', it is below the first floor (1).", thrown.getLocalizedMessage());
    }
    
    @Test
    @DisplayName("requesting floor above top floor raises an 'InvalidElevatorRequestException'")
    void requestFloorAboveTopRaisesException() {
        InvalidElevatorRequestException thrown = assertThrows(InvalidElevatorRequestException.class, () -> sut.moveToFloor(11));

        assertEquals("Cannot go to floor '11', it is above the top floor (10).", thrown.getLocalizedMessage());
    }
    
    @Test
    void descriptorHasDefaultId() {
        assertNotNull(BasicElevator.builder().build().describe().getId());
    }
    
    @Test
    void descriptorHasDefaultName() {
        assertEquals("Basic Elevator", BasicElevator.builder().build().describe().getName());
    }
    
    @Test
    void configureDescriptorName() {
        assertEquals("Elevator Monkey", BasicElevator.builder().name("Elevator Monkey").build().describe().getName());
    }
    
    @Test
    void defaultFirstFloorIs1() {
        assertEquals(1, BasicElevator.builder().build().getFirstFloor());
    }

    @Test
    void configureFirstFloorDifferently() {
        assertEquals(2, BasicElevator.builder().firstFloor(2).build().getFirstFloor());
    }
    
    @Test
    void defaultTopFloorIs10() {
        assertEquals(10, BasicElevator.builder().build().getTopFloor());
    }
    
    @Test
    void configureTopFloorDifferently() {
        assertEquals(20, BasicElevator.builder().topFloor(20).build().getTopFloor());
    }
    
    @Test
    void defaultSpeedIs1FloorPer3Seconds() {
        assertEquals(3, BasicElevator.builder().build().getMovementSpeed().getSeconds());
    }
    
    @Test
    void configureSpeedDifferently() {
        assertEquals(20, BasicElevator.builder().movementSpeed(Duration.ofSeconds(20)).build().getMovementSpeed().getSeconds());
    }   
    
    private <E> E captureEvent(Class type) {
        return captureEvent((it) -> type.isAssignableFrom(it.getClass()));
    }
    
    private <E> E captureEvent(Predicate<Object> matcher) {
        return (E) captureEvents(eventPublisher, eventCaptor, matcher, 1).get(0);
    }
    
    private <E> List<E> captureEvents(Predicate<Object> matcher, int expectedCount) {
        return captureEvents(eventPublisher, eventCaptor, matcher, expectedCount);
    }
}
