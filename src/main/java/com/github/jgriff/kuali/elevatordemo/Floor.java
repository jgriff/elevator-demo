package com.github.jgriff.kuali.elevatordemo;

import com.github.jgriff.kuali.elevatordemo.events.ElevatorConfirmEvent;
import reactor.core.publisher.Mono;

/**
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
public interface Floor {
    /** 
     * @return this floor's number (1 = first floor).
     * @since 0.0.0
     */
    int getNumber();

    /**
     * Initiates a request for an elevator to visit this floor and go UP.
     * 
     * @return a {@link Mono} that will signal the confirmation event once an elevator has been elected to answer our request.
     * @throws InvalidElevatorRequestException raised if this floor cannot make a request to go UP (ie, from the top floor).
     * @since 0.0.0
     */
    Mono<ElevatorConfirmEvent> requestUp() throws InvalidElevatorRequestException;
    
    /**
     * Initiates a request for an elevator to visit this floor and go DOWN.  
     *     
     * 
     * @return a {@link Mono} that will signal the confirmation event once an elevator has been elected to answer our request.
     * @throws InvalidElevatorRequestException raised if this floor cannot make a request to go DOWN (ie, from the first floor).
     * @since 0.0.0
     */
    Mono<ElevatorConfirmEvent> requestDown() throws InvalidElevatorRequestException;
}