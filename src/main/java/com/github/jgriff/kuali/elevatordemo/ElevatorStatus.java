package com.github.jgriff.kuali.elevatordemo;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Quick status of an {@link Elevator} at a snapshot in time.
 * 
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
@Value
@Builder
public class ElevatorStatus {
    /**
     * Unique identifier of the elevator this status is for.
     */
    private final ElevatorDescriptor descriptor;
    /**
     * Current operational state of the elevator, which indicates whether it can accept requests.
     */
    private final ElevatorOperationalState operationalState;
    /**
     * The floor the elevator is currently at. 
     */
    private final Integer currentFloor;
    /**
     * List of currently scheduled stops for the elevator, or empty if the elevator is untasked.  This will only
     * contain the floors the elevator is (currently) planning to stop at.
     */
    private final List<Integer> futureStops;
}
