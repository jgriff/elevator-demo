package com.github.jgriff.kuali.elevatordemo;

import com.github.jgriff.kuali.elevatordemo.events.ElevatorConfirmEvent;
import com.github.jgriff.kuali.elevatordemo.events.ElevatorRequestDownEvent;
import com.github.jgriff.kuali.elevatordemo.events.ElevatorRequestUpEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

/**
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
@SuppressWarnings("UnassignedFluxMonoInstance")
public class FloorTests implements MockitoTest, TimedTest {
    @Mock 
    private ApplicationEventPublisher eventPublisher;
    
    @InjectMocks 
    private BasicFloor sut = BasicFloor.builder().number(3).build();

    @Test
    @DisplayName("requesting UP from a floor results in publishing a 'ElevatorRequestUpEvent' with the floor's number")
    void requestUpPublishesElevatorRequestUpEvent() {
        sut.requestUp();
        
        // verify that our floor published the expected event
        verify(eventPublisher).publishEvent((Object)
            argThat(e -> ElevatorRequestUpEvent.class.cast(e).getRequestFloor() == 3)
        );
    }

    @Test
    @DisplayName("requesting DOWN from a floor results in publishing a 'ElevatorRequestDownEvent' with the floor's number")
    void requestDownPublishesElevatorRequestDownEvent() {
        sut.requestDown();
        
        // verify that our floor published the expected event
        verify(eventPublisher).publishEvent((Object)
            argThat(e -> ElevatorRequestDownEvent.class.cast(e).getRequestFloor() == 3)
        );
    }

    @Test
    @DisplayName("delivers the 'ElevatorRequestConfirmEvent' for UP requests back to the client who made the request")
    void requestUpReturnsElevatorRequestConfirmEvent() {
        // capture the request event, we'll need it's ID to generate a mock "confirm" event for it
        ArgumentCaptor<ElevatorRequestUpEvent> requestEventCaptor = ArgumentCaptor.forClass(ElevatorRequestUpEvent.class);
        
        // when: request is made
        Mono<ElevatorConfirmEvent> confirm = sut.requestUp();
        
        // then: the floor publishes a request event (which we are capturing with our mock so we can publish a mock 'confirm' event for it)
        verify(eventPublisher).publishEvent(requestEventCaptor.capture());
        
        // then: publish a mock confirmation (would normally come from the component responsible for electing an elevator to service our request)
        ElevatorConfirmEvent confirmEvent = ElevatorConfirmEvent.confirming(requestEventCaptor.getValue());
        // but first, throw in some noise to make sure we are filtering for just our expected confirmation
        sut.onElevatorRequestConfirm(ElevatorConfirmEvent.confirming(ElevatorRequestUpEvent.from(sut))); 
        sut.onElevatorRequestConfirm(confirmEvent); // THIS is the confirmation we should be looking for
        sut.onElevatorRequestConfirm(ElevatorConfirmEvent.confirming(ElevatorRequestUpEvent.from(sut))); // more noise... 
        
        // finally: verify the confirm event is delivered in the Mono we return to our caller
        ElevatorConfirmEvent delivered = confirm.block(timeout());
        assertEquals(confirmEvent, delivered, "Did not deliver confirm event in returned Mono.");
    }

    @Test
    @DisplayName("delivers the 'ElevatorRequestConfirmEvent' for DOWN requests back to the client who made the request")
    void requestDownReturnsElevatorRequestConfirmEvent() {
        // capture the request event, we'll need it's ID to generate a mock "confirm" event for it
        ArgumentCaptor<ElevatorRequestUpEvent> requestEventCaptor = ArgumentCaptor.forClass(ElevatorRequestUpEvent.class);
        
        // when: request is made
        Mono<ElevatorConfirmEvent> confirm = sut.requestDown();
        
        // then: the floor publishes a request event (which we are capturing with our mock so we can publish a mock 'confirm' event for it)
        verify(eventPublisher).publishEvent(requestEventCaptor.capture());
        
        // then: publish a mock confirmation (would normally come from the component responsible for electing an elevator to service our request)
        ElevatorConfirmEvent confirmEvent = ElevatorConfirmEvent.confirming(requestEventCaptor.getValue());
        // but first, throw in some noise to make sure we are filtering for just our expected confirmation
        sut.onElevatorRequestConfirm(ElevatorConfirmEvent.confirming(ElevatorRequestDownEvent.from(sut))); 
        sut.onElevatorRequestConfirm(confirmEvent); // THIS is the confirmation we should be looking for
        sut.onElevatorRequestConfirm(ElevatorConfirmEvent.confirming(ElevatorRequestDownEvent.from(sut))); // more noise... 
        
        // finally: verify the confirm event is delivered in the Mono we return to our caller
        ElevatorConfirmEvent delivered = confirm.block(timeout());
        assertEquals(confirmEvent, delivered, "Did not deliver confirm event in returned Mono.");
    }
    
    @Test
    @DisplayName("requesting DOWN from the first floor raises an 'InvalidElevatorRequestException'")
    void requestDownFromFirstFloorRaisesException() {
        Floor firstFloor = BasicFloor.builder().bottom(true).build();
        
        InvalidElevatorRequestException thrown = assertThrows(InvalidElevatorRequestException.class, firstFloor::requestDown);

        assertEquals("Cannot go DOWN from the first floor.", thrown.getLocalizedMessage());
    }

    @Test
    @DisplayName("by default, a floor is not the top floor")
    void notTopFloorByDefault() {
        BasicFloor defaultFloor = BasicFloor.builder().build();
        assertFalse(defaultFloor.isTop());
    }
    
    @Test
    @DisplayName("by default, a floor is not the bottom floor")
    void notBottomFloorByDefault() {
        BasicFloor defaultFloor = BasicFloor.builder().build();
        assertFalse(defaultFloor.isBottom());
    }
    
    @Test
    @DisplayName("a floor can be declared the top floor")
    void canConfigureAsTopFloor() {
        BasicFloor topFloor = BasicFloor.builder().top(true).build();
        assertTrue(topFloor.isTop());
    }
    
    @Test
    @DisplayName("a floor can be declared the bottom floor")
    void canConfigureAsBottomFloor() {
        BasicFloor bottomFloor = BasicFloor.builder().bottom(true).build();
        assertTrue(bottomFloor.isBottom());
    }
    
    @Test
    @DisplayName("a floor can be declared the top and bottom floor simultaneously (single story)")
    void canConfigureAsSingleStoryFloor() {
        BasicFloor singleStoryFloor = BasicFloor.builder().singleStory().build();
        assertTrue(singleStoryFloor.isTop(), "Should return true for isTop().");
        assertTrue(singleStoryFloor.isBottom(), "Should return true for isBottom().");
        assertEquals(1, singleStoryFloor.getNumber(), "Should be floor 1.");
    }
}
