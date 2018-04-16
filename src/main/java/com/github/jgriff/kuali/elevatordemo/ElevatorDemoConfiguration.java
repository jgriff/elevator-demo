package com.github.jgriff.kuali.elevatordemo;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
@Configuration
public class ElevatorDemoConfiguration {    
	
	private int numberOfFloors() { return 50; }
	private int numberOfElevators() { return 3; } 
	
	@Bean
	public List<Floor> floors(ApplicationEventPublisher eventPublisher) {
		final int numOfFloors = numberOfFloors();
		// 10 floors
		return Stream.iterate(1, i -> ++i).limit(numOfFloors)
				.map(floor -> BasicFloor.builder()
						.number(floor)
						.bottom(floor == 1)
						.top(floor == numOfFloors)
						.eventPublisher(eventPublisher)
						.build())
				.collect(Collectors.toList());
	}
	
	@Bean 
	public List<Elevator> elevators(ApplicationEventPublisher eventPublisher) {
		Random random = new Random(); // start the elevators at random floors
		final int numOfFloors = numberOfFloors();
		int numOfElevators = numberOfElevators();
		
		// create 1 elevators (easier to watch logs...choose any arbitrary number to scale up)
		return Stream.iterate(1, i -> ++i).limit(numOfElevators)
				.map(i -> BasicElevator.builder()
						.name("Elevator " + i)
						.topFloor(numOfFloors)
						.currentFloor(random.nextInt(numOfFloors))
						.eventPublisher(eventPublisher)
						.build()
				).collect(Collectors.toList());
	}  
}
