package com.github.jgriff.kuali.elevatordemo;

import lombok.Builder;
import lombok.Value;

/**
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */      
@Value
@Builder
public class ElevatorDiagnostic { 
    /**
     * Unique identifier of the elevator this diagnostic is for.
     */
    private final ElevatorDescriptor descriptor;
    /**
     * How many service calls this elevator has answered since being operational. 
     * A "trip" is one movement from the elevators starting floor to the floor it
     * is being called to.  A trip may pass several floors before terminating at
     * the calling floor (but can also be just one floor away).
     */
    private final Integer tripsMade;
    /**
     * How many floors this elevator has passed or visited since being operational.
     * This value will be greater than or equal to {@link #tripsMade}, since a trip
     * can require passing more than one floor before reaching the calling floor.
     */
    private final Integer floorsPassed;
}
