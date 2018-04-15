package com.github.jgriff.kuali.elevatordemo.events;

import com.github.jgriff.kuali.elevatordemo.ElevatorDescriptor;
import lombok.*;

/**
 * Event signaling an elevator is now moving.  The direction it is moving is given by {@link #getDirection()}. 
 * 
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
@Value  
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class ElevatorMovingEvent extends ElevatorOperationEvent {  
    /**
     * The floor the elevator is moving to.
     * @since 0.0.0
     */
    private final int nextFloor;
    /**
     * The floor being visited.
     * @since 0.0.0
     */
    private final Direction direction;

    @Builder
    public ElevatorMovingEvent(@NonNull ElevatorDescriptor elevator, @NonNull Direction direction, @NonNull Integer nextFloor) {
        super(elevator);
        this.direction = direction;
        this.nextFloor = nextFloor;
    }
    
    public enum Direction {
        UP,
        DOWN
    }
}
