package com.mindbug;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MindbugApplication {

	public static void main(String[] args) {
		SpringApplication.run(MindbugApplication.class, args);
	}
}
