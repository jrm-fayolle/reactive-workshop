package com.bonitasoft.reactiveworkshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class ReactiveWorkshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveWorkshopApplication.class, args);
	}

	@Bean
	RestTemplate client() {
		return new RestTemplateBuilder().rootUri(Constants.COMMENT_URI).build();
	}

	@Bean
	WebClient webClient() {
		return WebClient.create(Constants.COMMENT_URI);
	}
	
	
}
