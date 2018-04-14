package com.github.jgriff.kuali.elevatordemo.events;

import com.github.jgriff.kuali.elevatordemo.Floor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

/**
 * Event signaling a {@link Floor} is requesting an elevator to go UP.  
 * 
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @see ElevatorRequestDownEvent
 * @since 0.0.0
 */
@Value
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class ElevatorRequestUpEvent extends ElevatorRequestEvent {
    public ElevatorRequestUpEvent(int requestFloor) { 
        super(requestFloor);
    }
    
    public static ElevatorRequestUpEvent from(Floor requestFloor) {
        return new ElevatorRequestUpEvent(requestFloor.getNumber());
    }
}
