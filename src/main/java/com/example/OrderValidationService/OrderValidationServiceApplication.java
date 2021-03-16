package com.example.OrderValidationService;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class OrderValidationServiceApplication {

	@Bean
	public RestTemplate getRestTemplate(){
		return new RestTemplate();
	}

	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping("/")
	public String Hello() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("message", "Hello from Order-Validation-Service");
//		jsonObject.put("message-3",restTemplate.exchange("http://localhost:5003", HttpMethod.GET,
//				null, String.class).getBody());
		return jsonObject.toString();
	}

	public static void main(String[] args) {
		SpringApplication.run(OrderValidationServiceApplication.class, args);
	}
}
