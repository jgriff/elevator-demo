package com.github.jgriff.kuali.elevatordemo.events;

import lombok.NonNull;
import lombok.Value;

/**
 * Event signaling a confirmation response to requesting and elevator.  
 * 
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
@Value
public class ElevatorConfirmEvent {
    private final ElevatorRequestEvent confirmationOf;

    public boolean isConfirming(@NonNull ElevatorRequestEvent candidate) {
        return confirmationOf.getId().equals(candidate.getId());
    }
    
    public static ElevatorConfirmEvent confirming(@NonNull ElevatorRequestEvent request) {
        return new ElevatorConfirmEvent(request);
    }
}
