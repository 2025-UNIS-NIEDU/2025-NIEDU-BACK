package com.niedu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NieduApplication {

	public static void main(String[] args) {
		SpringApplication.run(NieduApplication.class, args);
	}

}
