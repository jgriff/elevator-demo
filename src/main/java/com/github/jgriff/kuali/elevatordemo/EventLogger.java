package com.github.jgriff.kuali.elevatordemo;

import com.github.jgriff.kuali.elevatordemo.events.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Simple listener that emits our events to the logger so we can see them during the
 * demo run.
 * 
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
@Component
@Slf4j
public class EventLogger {

    @EventListener
    public void onElevatorConfirmEvent(ElevatorConfirmEvent e) {
        log.info("Request confirmation received for elevator to go to floor '" + 
                e.getConfirmationOf().getRequestFloor() + "': " + e.getResult());
    }
    
    @EventListener
    public void onElevatorMovingEvent(ElevatorMovingEvent e) {
        log.info("Elevator '" + e.getElevator().getName() + "' is moving '" + 
                e.getDirection() + "' (next floor is '" + e.getNextFloor() + "')...");
    }
    
    @EventListener
    public void onElevatorFloorPassEvent(ElevatorFloorPassEvent e) {
        log.info("Elevator '" + e.getElevator().getName() + "' is passing floor '" + 
                e.getFloor() + "'...");
    }
    
    @EventListener
    public void onElevatorFloorStopEvent(ElevatorFloorStopEvent e) {
        log.info("Elevator '" + e.getElevator().getName() + "' has stopped at floor '" + 
                e.getFloor() + "'...");
    }
    
    @EventListener
    public void onElevatorDoorOpenEvent(ElevatorDoorOpenEvent e) {
        log.info("Elevator '" + e.getElevator().getName() + "' opened its doors at floor '" + 
                e.getFloor() + "'...");
    }
    
    @EventListener
    public void onElevatorDoorCloseEvent(ElevatorDoorCloseEvent e) {
        log.info("Elevator '" + e.getElevator().getName() + "' closed its doors at floor '" + 
                e.getFloor() + "'...");
    }
    
    @EventListener
    public void onElevatorDiagnosticUpdateEvent(ElevatorDiagnosticUpdateEvent e) {
        ElevatorDiagnostic d = e.getDiagnosticUpdate();
        log.info("Diagnostics for elevator '" + d.getDescriptor().getName() + "' received: " +
            "Total floors passed=" + d.getFloorsPassed() + ", Total trips=" + d.getTripsMade());
    }
}
