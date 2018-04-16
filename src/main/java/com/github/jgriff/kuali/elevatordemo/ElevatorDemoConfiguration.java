package com.github.jgriff.kuali.elevatordemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
@Configuration
public class ElevatorDemoConfiguration {
	@Bean public Elevator elevatorOne() 	{ return BasicElevator.builder().name("Elevator One").build(); }
	@Bean public Elevator elevatorTwo() 	{ return BasicElevator.builder().name("Elevator Two").build(); }
	@Bean public Elevator elevatorThree() 	{ return BasicElevator.builder().name("Elevator Three").build(); }
	@Bean public Elevator elevatorFour() 	{ return BasicElevator.builder().name("Elevator Four").build(); }    
}
