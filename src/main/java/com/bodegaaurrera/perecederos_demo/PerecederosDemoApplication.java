package com.bodegaaurrera.perecederos_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PerecederosDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(PerecederosDemoApplication.class, args);
	}

}
