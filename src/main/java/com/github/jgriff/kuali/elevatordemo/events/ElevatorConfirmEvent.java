package com.github.jgriff.kuali.elevatordemo.events;

import com.github.jgriff.kuali.elevatordemo.Identifiable;
import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

/**
 * Event signaling a confirmation response to requesting and elevator.  
 * 
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
@Value
public class ElevatorConfirmEvent implements Identifiable<UUID> {
    private final UUID id = UUID.randomUUID();
    private final ElevatorRequestEvent confirmationOf;
    private final Result result;

    public boolean isConfirming(@NonNull ElevatorRequestEvent candidate) {
        return confirmationOf.getId().equals(candidate.getId());
    }
    
    public static ElevatorConfirmEvent confirming(@NonNull ElevatorRequestEvent request) {
        return new ElevatorConfirmEvent(request, Result.SUCCESS);
    }
    
    public static ElevatorConfirmEvent noElevatorAvailableFor(@NonNull ElevatorRequestEvent request) {
        return new ElevatorConfirmEvent(request, Result.NO_AVAILABLE_ELEVATOR);
    }
    
    public static ElevatorConfirmEvent errorFor(@NonNull ElevatorRequestEvent request) {
        return new ElevatorConfirmEvent(request, Result.ERROR);
    }
    
    public enum Result {
        SUCCESS,
        NO_AVAILABLE_ELEVATOR,
        ERROR
    }
}
