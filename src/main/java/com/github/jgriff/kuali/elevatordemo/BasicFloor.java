package com.github.jgriff.kuali.elevatordemo;

import com.github.jgriff.kuali.elevatordemo.events.ElevatorConfirmEvent;
import com.github.jgriff.kuali.elevatordemo.events.ElevatorRequestDownEvent;
import com.github.jgriff.kuali.elevatordemo.events.ElevatorRequestEvent;
import com.github.jgriff.kuali.elevatordemo.events.ElevatorRequestUpEvent;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

/**
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
@Builder
@Getter
public class BasicFloor implements Floor {
    private final int number;
    private final boolean top, bottom;
    private ApplicationEventPublisher eventPublisher;
    
    private final Flux<ElevatorConfirmEvent> confirmations = Flux.create((FluxSink<ElevatorConfirmEvent> emitter) -> this.confirmationEmitter = emitter);
    private FluxSink<ElevatorConfirmEvent> confirmationEmitter;

    @Override
    public Mono<ElevatorConfirmEvent> requestUp() {
        if (isTop()) throw new InvalidElevatorRequestException("Cannot go UP from the top floor.");
        return publishForConfirm(ElevatorRequestUpEvent.from(this));
    }

    @Override
    public Mono<ElevatorConfirmEvent> requestDown() {
        if (isBottom()) throw new InvalidElevatorRequestException("Cannot go DOWN from the first floor.");
        return publishForConfirm(ElevatorRequestDownEvent.from(this));
    }

    /**
     * Support method that publishes a request and handles creating a {@link Mono} that will deliver the matching
     * {@link ElevatorConfirmEvent}.
     * 
     * @param request the request to publish, and expect a {@link ElevatorConfirmEvent} for.
     * @return a Mono to deliver the matching confirm event
     * @since 0.0.0
     */
    private Mono<ElevatorConfirmEvent> publishForConfirm(ElevatorRequestEvent request) {
        try {
            Mono<ElevatorConfirmEvent> toReturn = confirmations.publish().autoConnect()
                    .filter(e -> e.isConfirming(request)) // only deliver the confirmation for this particular request
                    .take(1).single() // expect only one confirm
                    .cache(); // cache it in case we get the event before our client (we are returning this to) subscribes

            toReturn.subscribe(); // induce demand so our Mono is activated downstream
            return toReturn;
        } finally {
            eventPublisher.publishEvent(request);
        }        
    } 

    @EventListener
    public void onElevatorRequestConfirm(ElevatorConfirmEvent e) {
        if (confirmationEmitter != null) {
            confirmationEmitter.next(e);
        } 
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder extends BasicFloorBuilder {
        public Builder singleStory() {
            return (Builder) number(1).top(true).bottom(true);
        }
    }
}
