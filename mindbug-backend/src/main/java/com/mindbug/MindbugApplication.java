package com.mindbug;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MindbugApplication {

	public static void main(String[] args) {
		SpringApplication.run(MindbugApplication.class, args);
	}

	@RequestMapping("/")
	String home() {
		return "Hello World!";
	}
	
}
