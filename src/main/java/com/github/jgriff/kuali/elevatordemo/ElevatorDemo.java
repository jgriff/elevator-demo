package com.github.jgriff.kuali.elevatordemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Random;

/**
 * Bean demonstrating the elevators in action!
 * 
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
@Component
@Slf4j
public class ElevatorDemo {
    @Autowired
    private List<Floor> floors;
    @Autowired
    private List<Elevator> elevators;

    private Random random = new Random();
    
    /**
     * Request an elevator from a random floor every 10 seconds.
     */
    @Scheduled(fixedRate = 10000)
    public void requestElevatorFromRandomFloor() {
        elevatorsInitialized();
        Floor floor = randomFloor();
        boolean up = random.nextBoolean();
        
        log.info("Making request from floor '" + floor.getNumber() + "' to go " + (up ? "up" : "down") + " ...");
        (up ? floor.requestUp() : floor.requestDown())
            .subscribe(confirm -> log.info("Receiving confirmation for elevator request from floor '" + floor.getNumber() + "': " + confirm));
    }
    
    private Floor randomFloor() {
        return floors.get(random.nextInt(floors.size()));
    }
    
    private boolean initialized;
    private void elevatorsInitialized() {
        if (!initialized) {
            elevators.forEach(e -> {
                if (e instanceof BasicElevator) {
                    ((BasicElevator)e).publishCurrentStatus();
                }
            });
            initialized = true;
        }
    }
}
