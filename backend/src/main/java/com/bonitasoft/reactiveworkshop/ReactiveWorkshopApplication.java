package com.bonitasoft.reactiveworkshop;

import javax.persistence.EntityManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ReactiveWorkshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveWorkshopApplication.class, args);
	}

	@Bean
	RestTemplate client() {
		return new RestTemplateBuilder().rootUri(Constants.COMMENT_URI).build();
	}

}
