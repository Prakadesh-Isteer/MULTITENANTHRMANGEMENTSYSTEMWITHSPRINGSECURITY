package com.isteer.util;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	  public static Logger logging = LogManager.getLogger(JwtUtil.class);

	    // A more secure secret key (use environment variables or secret manager in production)
	    private String secretKey = "3e8d497c3f8f3f2dBb6c8a72ad881b08a6b3fc43e7a0d76abf3cd@c34889d906";
	    
	    // Generate a JWT token for a given user with username and role
	    public String generateToken(String userName) {
	    	logging.info("Generating JWT token for user: {}", userName);
	    	Date issuedAt = new Date(System.currentTimeMillis());
	        Date expiration = new Date(System.currentTimeMillis() + 60 * 60 * 1000); // 1 hour expiration
	        logging.debug("Token issued at: {}", issuedAt);
	        logging.debug("Token expiration: {}", expiration);
	        logging.info("JWT token generated successfully for user: {}", userName);
	        return Jwts.builder()
	        		
	        // Add a custom claim for role (you can add more claims here)
	                .subject(userName)
	                .issuedAt(new Date(System.currentTimeMillis())) // Token issued timestamp
	                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000)) // Expiry time (1 hour from issuance)
	                .signWith(getKey())  // Sign the token with the secret key
	                .compact();  // Return the JWT token
	        
	    }
	    
	    // Generate secret key from a string
	    private SecretKey getKey() {    
	        logging.debug("Generating secret key from the provided secret key string");

	        byte[] byteArray = secretKey.getBytes();
	        return Keys.hmacShaKeyFor(byteArray);  // Create HMAC SHA key from secret
	    }
	    
	    // Extract the username (subject) from the token
	    public String extractUserName(String token) throws ExpiredJwtException {
	        logging.info("Extracting username from JWT token");

	        return extractClaim(token, Claims::getSubject);
	    }
	    
	    // Extract specific claims from the token (generic method)
	    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
	        logging.debug("Extracting all claims from the JWT token");

	        final Claims claims = extractAllClaims(token);
	        return claimResolver.apply(claims);
	    }
	    
	    // Extract all claims from the token
	    private Claims extractAllClaims(String token) {
	            return Jwts.parser()
	                    .verifyWith(getKey())  // Use the signing key
	                    .build()
	                    .parseSignedClaims(token)  // Parse the JWT token
	                    .getPayload();  // Get the claims body
	    }
	    // Validate the token: check if it matches the username and if it's expired
	    public boolean validateToken(String token, UserDetails userDetails) {
	        logging.info("Validating JWT token...");
	        final String userName = extractUserName(token);
            logging.info("JWT token is valid for user: {}", userName);

	        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
	    }
	    
	    // Check if the token is expired
	    private boolean isTokenExpired(String token) {
	    	  Date expirationDate = extractExpiration(token);
	          boolean isExpired = expirationDate.before(new Date());
	          
	          logging.debug("Token expiration date: {}", expirationDate);
	          logging.debug("Is token expired: {}", isExpired);
	        return extractExpiration(token).before(new Date());  // Check if the expiration date is in the past
	    }

	    // Extract expiration date from the token
	    private Date extractExpiration(String token) {
	        return extractClaim(token, Claims::getExpiration);
	    }
}
