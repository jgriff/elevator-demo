package com.github.jgriff.kuali.elevatordemo.events;

import com.github.jgriff.kuali.elevatordemo.Floor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

/**
 * Event signaling a {@link Floor} is requesting an elevator to go DOWN.  
 * 
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @see ElevatorRequestUpEvent
 * @since 0.0.0
 */
@Value
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class ElevatorRequestDownEvent extends ElevatorRequestEvent {
    public ElevatorRequestDownEvent(int requestFloor) { super(requestFloor); }
    
    public static ElevatorRequestDownEvent from(Floor requestFloor) {
        return new ElevatorRequestDownEvent(requestFloor.getNumber());
    }
}
