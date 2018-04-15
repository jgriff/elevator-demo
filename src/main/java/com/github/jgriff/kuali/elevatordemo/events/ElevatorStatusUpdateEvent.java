package com.github.jgriff.kuali.elevatordemo.events;

import com.github.jgriff.kuali.elevatordemo.ElevatorStatus;
import com.github.jgriff.kuali.elevatordemo.Identifiable;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

/**
 * Event containing the current status of a particular {@link com.github.jgriff.kuali.elevatordemo.Elevator}.  Elevators
 * must produce this event whenever a substantial state change occurs.  The following are considered "substantial" to
 * warrant this event:
 * <ul>
 *     <li>{@link ElevatorStatus#getOperationalState() Operational state} changes.</li>
 *     <li>The {@link ElevatorStatus#getCurrentFloor() current floor} the elevator is at changes.</li>
 *     <li>The {@link ElevatorStatus#getFutureStops() list of future stops} is updated.</li>
 * </ul>
 * 
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
@Value
@Builder
public class ElevatorStatusUpdateEvent implements Identifiable<UUID> {
    private final UUID id = UUID.randomUUID();
    private final ElevatorStatus statusUpdate;

    public static ElevatorStatusUpdateEvent forStatus(ElevatorStatus status) {
        return new ElevatorStatusUpdateEvent(status);
    }
}
