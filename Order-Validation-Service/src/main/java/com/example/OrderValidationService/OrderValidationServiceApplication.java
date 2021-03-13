package com.example.OrderValidationService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class OrderValidationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderValidationServiceApplication.class, args);
	}
	@RequestMapping("/")
	public String home(){
		return "Hi there";
	}
}
