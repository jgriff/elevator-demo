package com.github.jgriff.kuali.elevatordemo.events;

import com.github.jgriff.kuali.elevatordemo.ElevatorDescriptor;
import lombok.*;

/**
 * Event signaling an elevator is passing a particular floor, without stopping at it.
 * 
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
@Value  
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class ElevatorFloorPassEvent extends ElevatorOperationEvent {
    /**
     * The floor being passed.
     * @since 0.0.0
     */
    private final int floor;

    @Builder
    public ElevatorFloorPassEvent(@NonNull ElevatorDescriptor elevator, int floor) {
        super(elevator);
        this.floor = floor;
    }
}
