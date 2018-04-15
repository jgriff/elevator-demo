package com.github.jgriff.kuali.elevatordemo.events;

import com.github.jgriff.kuali.elevatordemo.ElevatorDiagnostic;
import com.github.jgriff.kuali.elevatordemo.Identifiable;
import lombok.Value;

import java.util.UUID;

/**
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
@Value
public class ElevatorDiagnosticUpdateEvent implements Identifiable<UUID> {
    private final UUID id = UUID.randomUUID(); 
    private final ElevatorDiagnostic diagnosticUpdate;

    public static ElevatorDiagnosticUpdateEvent forDiagnostic(ElevatorDiagnostic diagnostic) {
        return new ElevatorDiagnosticUpdateEvent(diagnostic);
    }
}
