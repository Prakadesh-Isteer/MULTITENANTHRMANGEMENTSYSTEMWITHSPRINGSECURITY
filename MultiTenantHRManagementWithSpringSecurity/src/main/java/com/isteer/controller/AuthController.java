package com.isteer.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

	public static Logger logging = LogManager.getLogger(AuthController.class);

	@Autowired
	AuthenticationManager manager;

	@Autowired
	AuthService service;

	@Autowired
	RedisService redisService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserDetailsDto users) {
		logging.info("Attempting login for user: {}", users.getUserName());
		String token = service.userLogin(users);
		String previousToken = redisService.getLatestToken(users.getUserName());

		if (previousToken != null) {
			logging.info("Previous token found, removing it for user: {}", users.getUserName());
			redisService.removeToken(previousToken);
		}
		redisService.putUpdatedToken(users.getUserName(), token);

		JwtResponse wrktoken = new JwtResponse(token);
		logging.info("Login successful for user: {}", users.getUserName());
		return ResponseEntity.ok(wrktoken);

	}

//    @PreAuthorize("@authService.hasPermission()")
	@GetMapping("/me")
	public ResponseEntity<?> getUsersLoggedIn(String userId) {
		logging.info("Fetching logged-in user details for userId: {}", userId);
		Employee wrkUserName = service.getuserByLogged(userId);
		if (wrkUserName == null) {
			logging.warn("No user found with userId: {}", userId);
			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.NO_USERS_FOUND_LIST.getStatusCode(),
					HrManagementEnum.NO_USERS_FOUND_LIST.getStatusMessage());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);

		}
		logging.info("User found: {}", wrkUserName);
		return ResponseEntity.ok(wrkUserName);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logoutMethod(@RequestHeader("Authorization") String authorizationToken) {

		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		logging.info("Logging out user: {}", userName);
		if (authorizationToken != null && authorizationToken.startsWith("Bearer ")) {
			String token = authorizationToken.substring(7);

			if (token.equals(redisService.getLatestToken(userName))) {
				redisService.removeToken(userName);
				StatusMessageDto message = new StatusMessageDto(HrManagementEnum.LOGOUT_SUCCESS.getStatusCode(),
						HrManagementEnum.LOGOUT_SUCCESS.getStatusMessage());
				logging.info("Logout successful for user: {}", userName);
				return ResponseEntity.status(HttpStatus.OK).body(message);
			}
		}

		ErrorMessageDto errorMessage = new ErrorMessageDto(HrManagementEnum.LOGOUT_FAILED.getStatusCode(),
				HrManagementEnum.LOGOUT_FAILED.getStatusMessage());
		logging.warn("Logout failed for user: {}", userName);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
	}

	@PreAuthorize("@authService.hasPermission()")
	@PostMapping("/addUrl")
	public ResponseEntity<?> addUrl(@RequestParam String endpointUrl, @RequestParam String roleUuid) {
		logging.info("Adding endpoint URL: {} with role UUID: {}", endpointUrl, roleUuid);

		try {
			logging.info(" Call the service method to add the endpoint and role mapping");
			int status = service.addEndpointWithRoleMapping(endpointUrl, roleUuid);

			if (status > 0) {
				logging.info("Endpoint URL and role mapping added successfully.");
				StatusMessageDto message = new StatusMessageDto(HrManagementEnum.END_POINT_CREATED.getStatusCode(),
						HrManagementEnum.END_POINT_CREATED.getStatusMessage());
				return ResponseEntity.status(HttpStatus.OK).body(message);
			}

			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.END_POINT_FAILED.getStatusCode(),
					HrManagementEnum.END_POINT_FAILED.getStatusMessage());
			logging.error("Failed to add endpoint URL and role mapping.");

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

		} catch (Exception e) {
			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.END_POINT_FAILED.getStatusCode(),
			  e.getMessage());
			logging.error("Error occurred while adding endpoint URL and role mapping: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}
	}

	@PreAuthorize("@authService.hasPermission()")
	@PostMapping("/addHttpMethod")
	public ResponseEntity<?> addHttpMethodMapping(@RequestParam String httpMethod, @RequestParam String roleUuid) {
		logging.info("Mapping HTTP method: {} with role UUID: {}", httpMethod, roleUuid);
		try {

			int status = service.addHttpMethodMapping(httpMethod, roleUuid);

			if (status > 0) {
				logging.info("HTTP method mapping added successfully.");
				StatusMessageDto successMessage = new StatusMessageDto(
						HrManagementEnum.METHOD_MAPPING_SUCCESS.getStatusCode(),
						HrManagementEnum.METHOD_MAPPING_SUCCESS.getStatusMessage());
				return ResponseEntity.status(HttpStatus.OK).body(successMessage);
			} else {
				logging.error("Failed to add HTTP method mapping.");

				ErrorMessageDto errorMessage = new ErrorMessageDto(HrManagementEnum.METHOD_MAPPING_FAIL.getStatusCode(),
						HrManagementEnum.METHOD_MAPPING_FAIL.getStatusMessage());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
			}

		} catch (Exception e) {
			logging.error("Error occurred while adding HTTP method mapping: {}", e.getMessage());
			ErrorMessageDto errorMessage = new ErrorMessageDto(HrManagementEnum.METHOD_MAPPING_FAIL.getStatusCode(),
					"Error: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		}
	}

}
