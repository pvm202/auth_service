package com.auth_service.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.auth_service.models.AuthRequest;
import com.auth_service.service.UsermanagementService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsermanagementService usermanagementService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping(value = "/login")
    public ResponseEntity<Object> login(@RequestBody AuthRequest authRequest) {
        logger.info("Received login request for user: {}", authRequest.getUserName());

        try {
            ResponseEntity<Boolean> isValid = this.usermanagementService.validCred(authRequest);	
            	
            if (isValid.getStatusCode().is2xxSuccessful() && Boolean.TRUE.equals(isValid.getBody())) {
                logger.info("User validated successfully, generating token for user: {}", authRequest.getUserName());
                
                
                // Generate token logic (not implemented here)
                
                return ResponseEntity.status(HttpStatus.OK).body("Token generated");
                
                
            } 
            else {
                logger.error("invalid credentials");
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid credentials");
            }
        }
            catch (HttpClientErrorException.NotFound e) {
                logger.error("Unauthorized access to user management service", e);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
                
            }
         catch (Exception e) {
            logger.error("An error occurred while validating user: {}", authRequest.getUserName(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request");
        }
    }
}
