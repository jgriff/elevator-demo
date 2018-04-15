package com.github.jgriff.kuali.elevatordemo;

/**
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
public interface Elevator {
    /**
     * Describe this elevator, using a {@link ElevatorDescriptor}.
     * 
     * @return a descriptor describing this elevator.
     * @since 0.0.0
     */
    ElevatorDescriptor describe();

    /**
     * Request this elevator to move to a specific floor.  If this elevator is in motion, or has floors it is planning
     * to visit, this floor will be added to the end of the visitation queue.
     * 
     * @param floor a floor to schedule this elevator to visit.
     * @throws InvalidElevatorRequestException raise if the request floor is invalid (ie, below the first floor or above the top floor).
     * @since 0.0.0
     */
    void moveToFloor(int floor) throws InvalidElevatorRequestException;
}
