package com.isteer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.isteer.dto.ErrorMessageDto;
import com.isteer.dto.JwtResponse;
import com.isteer.dto.StatusMessageDto;
import com.isteer.dto.UserDetailsDto;
import com.isteer.entity.Employee;
import com.isteer.enums.HrManagementEnum;
import com.isteer.service.AuthService;
import com.isteer.service.RedisService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
	@Autowired
	AuthenticationManager manager;
 
    @Autowired
    AuthService service;
    
    @Autowired
    RedisService redisService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDetailsDto users) {
    	String token = service.userLogin(users);
    	String previousToken = redisService.getLatestToken(users.getUserName());
    	
    	if(previousToken != null) {
    		redisService.removeToken(previousToken);
    	}
    	redisService.putUpdatedToken(users.getUserName(), token);
    	
    	JwtResponse wrktoken = new JwtResponse(token);   
		return ResponseEntity.ok(wrktoken);
    	   	
}
    
    
//    @PreAuthorize("@authService.hasPermission()")
	@GetMapping("/me")
	public ResponseEntity<?> getUsersLoggedIn(String userId) {
		Employee single = service.getuserByLogged(userId);
		if(single == null) {
			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.NO_USERS_FOUND_LIST.getStatusCode(),
					HrManagementEnum.NO_USERS_FOUND_LIST.getStatusMessage());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
		
		}
		return ResponseEntity.ok(single);
	}
    
    @PostMapping("/logout")
    public ResponseEntity<?> logoutMethod(@RequestHeader("Authorization") String authorizationToken) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        if (authorizationToken != null && authorizationToken.startsWith("Bearer ")) {
            String token = authorizationToken.substring(7);
            
            if (token.equals(redisService.getLatestToken(userName))) {
                redisService.removeToken(userName);
                StatusMessageDto message = new StatusMessageDto(
                        HrManagementEnum.LOGOUT_SUCCESS.getStatusCode(),
                        HrManagementEnum.LOGOUT_SUCCESS.getStatusMessage());
                return ResponseEntity.status(HttpStatus.OK).body(message);
            }
        }

        ErrorMessageDto errorMessage = new ErrorMessageDto(
                HrManagementEnum.LOGOUT_FAILED.getStatusCode(),
                HrManagementEnum.LOGOUT_FAILED.getStatusMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    
    
	@PreAuthorize("@authService.hasPermission()")
    @PostMapping("/addUrl")
    public ResponseEntity<?> addUrl(@RequestParam String endpointUrl, @RequestParam String roleUuid) {

        try {
            // Call the service method to add the endpoint and role mapping
            int status = service.addEndpointWithRoleMapping(endpointUrl, roleUuid);
            
            // Return success response if the mapping is added successfully
            if (status > 0) {
                StatusMessageDto message = new StatusMessageDto(
                        HrManagementEnum.END_POINT_CREATED.getStatusCode(),
                        HrManagementEnum.END_POINT_CREATED.getStatusMessage());
                return ResponseEntity.status(HttpStatus.OK).body(message);
            }
            
            // Return failure response if adding mapping failed
            ErrorMessageDto error = new ErrorMessageDto(
                    HrManagementEnum.END_POINT_FAILED.getStatusCode(),
                    HrManagementEnum.END_POINT_FAILED.getStatusMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            
        } catch (Exception e) {
            // Catch any exception and return an error response
            ErrorMessageDto error = new ErrorMessageDto(
                    HrManagementEnum.END_POINT_FAILED.getStatusCode(),
                    "Error adding endpoint: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
	@PreAuthorize("@authService.hasPermission()")
 // Endpoint to map HTTP method to role using UUID
    @PostMapping("/addHttpMethod")
    public ResponseEntity<?> addHttpMethodMapping(@RequestParam String httpMethod, @RequestParam String roleUuid) {
        try {
            // Call service method to add the HTTP method mapping
            int status = service.addHttpMethodMapping(httpMethod,roleUuid);

            if (status > 0) {
                StatusMessageDto successMessage = new StatusMessageDto(
                        HrManagementEnum.METHOD_MAPPING_SUCCESS.getStatusCode(),
                        HrManagementEnum.METHOD_MAPPING_SUCCESS.getStatusMessage());
                return ResponseEntity.status(HttpStatus.OK).body(successMessage);
            } else {
                ErrorMessageDto errorMessage = new ErrorMessageDto(
                        HrManagementEnum.METHOD_MAPPING_FAIL.getStatusCode(),
                        HrManagementEnum.METHOD_MAPPING_FAIL.getStatusMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }

        } catch (Exception e) {
            // Handle any exception that occurs
        	e.printStackTrace();
            ErrorMessageDto errorMessage = new ErrorMessageDto(
                    HrManagementEnum.METHOD_MAPPING_FAIL.getStatusCode(),
                    "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
    }
    
    
}
