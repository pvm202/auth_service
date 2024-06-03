package com.auth_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.auth_service.models.AuthRequest;

@Service
public class UsermanagementService {

	@Autowired
	private RestTemplate restTemplate;

	private static final Logger logger = LoggerFactory.getLogger(UsermanagementService.class);

	public ResponseEntity<Boolean> validCred(AuthRequest authRequest) {
		// URL of the user management service
		String url = "http://localhost:8080/auth/validate";
		try {
			logger.info("Sending request to user management service for validation");

			// Send API call to user management service to validate user
			ResponseEntity<Boolean> response = restTemplate.postForEntity(url, authRequest, Boolean.class);

			System.out.println(response);

			if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
				logger.info("Received successful response from user management service");
				return ResponseEntity.ok(response.getBody());
			}

			logger.warn("User validation failed with status code: " + response.getStatusCode());
			return ResponseEntity.status(response.getStatusCode()).body(false);
			
			
			

		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				logger.warn("User not found in the user management service");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
			} else {
				logger.error("Error communicating with user management service", e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
			}
		} catch (HttpServerErrorException e) {
			if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
				logger.warn("something went wrong  internal server error in usermanagement service");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
			} else {
				logger.error("Error communicating with user management service", e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
			}
		} catch (Exception e) {
			logger.error("An error occurred while communicating with usermanagement service", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
	}

}
