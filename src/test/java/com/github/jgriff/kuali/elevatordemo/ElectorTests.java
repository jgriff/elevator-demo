package com.github.jgriff.kuali.elevatordemo;

import com.github.jgriff.kuali.elevatordemo.events.ElevatorConfirmEvent;
import com.github.jgriff.kuali.elevatordemo.events.ElevatorRequestDownEvent;
import com.github.jgriff.kuali.elevatordemo.events.ElevatorRequestUpEvent;
import com.github.jgriff.kuali.elevatordemo.events.ElevatorStatusUpdateEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
public class ElectorTests implements MockitoTest, EventTest { 
    @Mock 
    private ApplicationEventPublisher eventPublisher;
    private ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
    
    @Mock private Elevator elevatorOne;
    private ElevatorDescriptor elevatorOneDescriptor = ElevatorDescriptor.builder().id(UUID.randomUUID()).name("Elevator One").build();
    @Mock private Elevator elevatorTwo;
    private ElevatorDescriptor elevatorTwoDescriptor = ElevatorDescriptor.builder().id(UUID.randomUUID()).name("Elevator One").build();
    
    @InjectMocks
    private ElevatorElector sut = new ElevatorElector();
    
    @BeforeEach
    void configureMockElevators() {
        when(elevatorOne.describe()).thenReturn(elevatorOneDescriptor);
        when(elevatorTwo.describe()).thenReturn(elevatorTwoDescriptor);
        
        sut.setElevators(Arrays.asList(elevatorOne, elevatorTwo));
    }
    
    @Test
    void choosesClosestElevatorForUPRequest() {
        // given: 2 elevators in operation
        sut.onElevatorStatusUpdate(ElevatorStatusUpdateEvent.forStatus(ElevatorStatus.builder()
            .descriptor(elevatorOneDescriptor)
            .operationalState(ElevatorOperationalState.OPERATIONAL)
            .currentFloor(3)
            .build()
        ));
        sut.onElevatorStatusUpdate(ElevatorStatusUpdateEvent.forStatus(ElevatorStatus.builder()
            .descriptor(elevatorTwoDescriptor)
            .operationalState(ElevatorOperationalState.OPERATIONAL)
            .currentFloor(8)
            .build()
        ));
        
        // when: a request arrives
        ElevatorRequestUpEvent request = new ElevatorRequestUpEvent(9);
        sut.onElevatorRequestUp(request);
        
        // then: the elector chooses the nearest elevator (which is elevator two) 
        Mockito.verify(elevatorTwo, Mockito.timeout(timeout().toMillis())).moveToFloor(9);
        
        // and: publishes a confirmation for the request
        ElevatorConfirmEvent expectedConfirmation = captureEvent(ElevatorConfirmEvent.class);
        
        // and: the event confirms the original request
        assertTrue(expectedConfirmation.isConfirming(request), "Confirmation did not carry the original request.");
    }
    
    @Test
    void choosesClosestElevatorForDownRequest() {
        // given: 2 elevators in operation
        sut.onElevatorStatusUpdate(ElevatorStatusUpdateEvent.forStatus(ElevatorStatus.builder()
            .descriptor(elevatorOneDescriptor)
            .operationalState(ElevatorOperationalState.OPERATIONAL)
            .currentFloor(3)
            .build()
        ));
        sut.onElevatorStatusUpdate(ElevatorStatusUpdateEvent.forStatus(ElevatorStatus.builder()
            .descriptor(elevatorTwoDescriptor)
            .operationalState(ElevatorOperationalState.OPERATIONAL)
            .currentFloor(8)
            .build()
        ));
        
        // when: a request arrives
        ElevatorRequestDownEvent request = new ElevatorRequestDownEvent(4);
        sut.onElevatorRequestDown(request);
        
        // then: the elector chooses the nearest elevator (which is elevator one) 
        Mockito.verify(elevatorOne, Mockito.timeout(timeout().toMillis())).moveToFloor(4);
        
        // and: publishes a confirmation for the request
        ElevatorConfirmEvent expectedConfirmation = captureEvent(ElevatorConfirmEvent.class);
        
        // and: the event confirms the original request
        assertTrue(expectedConfirmation.isConfirming(request), "Confirmation did not carry the original request.");
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
