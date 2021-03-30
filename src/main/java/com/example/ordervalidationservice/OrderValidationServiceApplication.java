package com.example.ordervalidationservice;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class OrderValidationServiceApplication {

	@RequestMapping("/")
	public String Hello() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("message", "Hello from Order-Validation-Service jjj");
		return jsonObject.toString();
	}

	public static void main(String[] args) {
		SpringApplication.run(OrderValidationServiceApplication.class, args);
	}
}
