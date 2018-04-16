package com.github.jgriff.kuali.elevatordemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
@SpringBootApplication
@EnableScheduling
public class Main {
	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}
}