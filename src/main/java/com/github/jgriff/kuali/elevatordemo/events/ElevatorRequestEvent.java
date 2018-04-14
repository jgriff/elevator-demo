package com.github.jgriff.kuali.elevatordemo.events;

import com.github.jgriff.kuali.elevatordemo.Identifiable;
import lombok.Data;

import java.util.UUID;

/**
 * Generic request for an elevator.  The requesting floor is identified by {@link #getRequestFloor()}.
 * 
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
@Data
public abstract class ElevatorRequestEvent implements Identifiable<UUID> {
    private final UUID id = UUID.randomUUID();
    private final int requestFloor;
    
    public ElevatorRequestEvent(int requestFloor) { 
        this.requestFloor = requestFloor;
    }
}
