package com.biancapasch.poc.gym_checkin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GymCheckinApplication {

	public static void main(String[] args) {
		SpringApplication.run(GymCheckinApplication.class, args);
	}

}
