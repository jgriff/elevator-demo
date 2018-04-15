package com.github.jgriff.kuali.elevatordemo.events;

import com.github.jgriff.kuali.elevatordemo.ElevatorDescriptor;
import lombok.*;

/**
 * Event signaling an elevator has opened its doors.
 * 
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
@Value   
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class ElevatorDoorOpenEvent extends ElevatorOperationEvent { 
    /**
     * The floor the door is opening on.
     * @since 0.0.0
     */
    private final int floor;

    @Builder
    public ElevatorDoorOpenEvent(@NonNull ElevatorDescriptor elevator, int floor) {
        super(elevator);
        this.floor = floor;
    }
}
