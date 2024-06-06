package com.auth_service.jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer.JwtConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.auth_service.models.AuthRequest;
import com.auth_service.service.UsermanagementService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
@SuppressWarnings({ "deprecation", "unused" })
@Component
public class JwtHelper {
	
	
	private static final Logger logger = LoggerFactory.getLogger(UsermanagementService.class);


	@Value("${jwt.expiration}")
	private long expiration;
	
	@Value("${jwt.secret}")
	private String secret;
	
	
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

	
    //for retrieveing any information from token we will need the secret key
	   // For retrieving any information from the token, we need the secret key
    private Claims getAllClaimsFromToken(String token) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    
    //generate token
    
    public String generateToken(AuthRequest authRequest ) {
    	
    	Map<String, Object> claims=new HashMap<>();
    	return doGenerateToken(claims,authRequest.getUserName());
    	
    }

    //do generate token
	private String doGenerateToken(Map<String, Object> claims, String subject) {
		
		
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis())).
				setExpiration(new Date(System.currentTimeMillis()+expiration)).signWith(SignatureAlgorithm.HS512, secret).compact()
				;
	}
	
	 //validate token
    public Boolean validateToken(String token,  AuthRequest authRequest) {
        final String username = getUsernameFromToken(token);
        return (username.equals(authRequest.getUserName()) && !isTokenExpired(token));
    }


	private String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}


	


	private boolean isTokenExpired(String token) {
		   final Date expiration = getExpirationDateFromToken(token);
	        return expiration.before(new Date(System.currentTimeMillis()));
	}


	private Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	
}
