package com.auth_service.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;



@Configuration
public class RestTemplateConfig {
	
	
	
	//for authenticating login endpoint
	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
	

}
