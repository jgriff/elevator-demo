package com.github.jgriff.kuali.elevatordemo.events;

import com.github.jgriff.kuali.elevatordemo.ElevatorDescriptor;
import lombok.*;

/**
 * Event signaling an elevator is stopping at a particular floor.  A "stop" means the elevator is stopping at this
 * floor and will open its doors (see {@link ElevatorDoorOpenEvent}.
 * 
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
@Value  
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class ElevatorFloorStopEvent extends ElevatorOperationEvent {
    /**
     * The floor being visited.
     * @since 0.0.0
     */
    private final int floor;

    @Builder
    public ElevatorFloorStopEvent(@NonNull ElevatorDescriptor elevator, int floor) {
        super(elevator);
        this.floor = floor;
    }
}
