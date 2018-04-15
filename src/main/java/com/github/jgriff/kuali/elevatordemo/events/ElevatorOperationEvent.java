package com.github.jgriff.kuali.elevatordemo.events;

import com.github.jgriff.kuali.elevatordemo.ElevatorDescriptor;
import com.github.jgriff.kuali.elevatordemo.Identifiable;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

/**
 * Generic event for any elevator operation, such as {@link ElevatorFloorStopEvent stopping at floors}, 
 * {@link ElevatorDoorOpenEvent opening} and {@link ElevatorDoorCloseEvent closing} its doors, etc.
 * 
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
@Data
public abstract class ElevatorOperationEvent implements Identifiable<UUID> {
    private final UUID id = UUID.randomUUID();
    /**
     * The elevator this operation pertains to.
     * @since 0.0.0
     */
    private final ElevatorDescriptor elevator;

    public ElevatorOperationEvent(@NonNull ElevatorDescriptor elevator) {
        this.elevator = elevator;
    }
}
