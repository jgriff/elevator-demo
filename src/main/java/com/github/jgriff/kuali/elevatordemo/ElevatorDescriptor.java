package com.github.jgriff.kuali.elevatordemo;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

/**
 * Simple descriptor for describing an elevator using the elevator's {@link #id} and {@link #name}.  Could expand to
 * other useful things later if needed.
 * 
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
@Value
@Builder
public class ElevatorDescriptor {
    private final UUID id;
    private final String name;
}
