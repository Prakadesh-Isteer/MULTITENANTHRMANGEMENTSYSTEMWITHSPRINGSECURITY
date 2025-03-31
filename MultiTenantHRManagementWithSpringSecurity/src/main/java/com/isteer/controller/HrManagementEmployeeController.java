package com.isteer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import com.isteer.dto.ErrorMessageDto;
import com.isteer.dto.JwtResponse;
import com.isteer.dto.StatusMessageDto;
import com.isteer.dto.UserDetailsDto;
import com.isteer.entity.Employee;
import com.isteer.entity.Roles;
import com.isteer.enums.HrManagementEnum;
import com.isteer.service.CustomerUserDetailsService;
import com.isteer.service.HrManagementEmployeeService;
import com.isteer.util.JwtUtil;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/hrManagement")
public class HrManagementEmployeeController {
	
	
	@Autowired
	HrManagementEmployeeService service;
	
	@PreAuthorize("@authService.hasPermission()")
	@PostMapping("user")
	public ResponseEntity<?> registerUser(@RequestParam String departmentUuid, @Valid @RequestBody UserDetailsDto details) {
	
		int status = service.registerUser(details, departmentUuid);
		if(status > 0) {
			 StatusMessageDto message = new StatusMessageDto(
		                HrManagementEnum.USER_CREATED_SUCCESS.getStatusCode(),
		                HrManagementEnum.USER_CREATED_SUCCESS.getStatusMessage());
		        return ResponseEntity.status(HttpStatus.OK).body(message);
		}
		  ErrorMessageDto error = new ErrorMessageDto(
		            HrManagementEnum.USER_FAILED_CREATION.getStatusCode(),
		            HrManagementEnum.USER_FAILED_CREATION.getStatusMessage());
		    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@PreAuthorize("@authService.hasPermission()")
	@PostMapping("addRole")
	public ResponseEntity<?> addRole(@Valid @RequestBody Roles role) {
		int status = service.addRole(role);
		if(status > 0) {
			 StatusMessageDto message = new StatusMessageDto(
		                HrManagementEnum.Role_added.getStatusCode(),
		                HrManagementEnum.Role_added.getStatusMessage());
		        return ResponseEntity.status(HttpStatus.OK).body(message);
		}
		  ErrorMessageDto error = new ErrorMessageDto(
		            HrManagementEnum.Role_not_added.getStatusCode(),
		            HrManagementEnum.Role_not_added.getStatusMessage());
		    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		
	}
	@PreAuthorize("@authService.hasPermission()")
	@GetMapping("users")
	public ResponseEntity<?> getAllUsers(){
		List<?> list = service.getAllUsers();
		if(list.isEmpty()) {
			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.NO_USERS_FOUND_LIST.getStatusCode(),
					HrManagementEnum.NO_USERS_FOUND_LIST.getStatusMessage());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
		
		}
		return ResponseEntity.ok(list) ;
		
	}
	@PreAuthorize("@authService.hasPermission()")
	@GetMapping("user")
	public ResponseEntity<?> getUsersById(String userId) {
		Employee single = service.getuserById(userId);
		if(single == null) {
			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.NO_USERS_FOUND_LIST.getStatusCode(),
					HrManagementEnum.NO_USERS_FOUND_LIST.getStatusMessage());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
		
		}
		return ResponseEntity.ok(single);
	}
	
	@PreAuthorize("@authService.hasPermission()")
	@PutMapping("user")
	public ResponseEntity<?> updateUser(@RequestParam String employeeUuid, @Valid @RequestBody UserDetailsDto details) {
		details.setEmployeeUuid(employeeUuid);
		int status = service.updateUser(details);
	
		if (status > 0) {
			StatusMessageDto message = new StatusMessageDto(
					HrManagementEnum.USER_UPDATED_SUCCESS.getStatusCode(),
					HrManagementEnum.USER_UPDATED_SUCCESS.getStatusMessage());
			return ResponseEntity.status(HttpStatus.OK).body(message);
		} else if (status == -1) {
			// User not found, invalid tenantId
			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.USER_VALID_NOT_FOUND.getStatusCode(),
					HrManagementEnum.USER_VALID_NOT_FOUND.getStatusMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}

		ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.USER_UPDATE_FAIL.getStatusCode(),
				HrManagementEnum.USER_UPDATE_FAIL.getStatusMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	
	}
	
	@PreAuthorize("@authService.hasPermission()")
	@DeleteMapping("user")
	public ResponseEntity<?> deleteEmployee(@RequestParam String employeeUuid){
		
		int status = service.deleteEmployee(employeeUuid);
		
		if (status > 0) {
			StatusMessageDto message = new StatusMessageDto(
					HrManagementEnum.User_deleted_success.getStatusCode(),
					HrManagementEnum.User_deleted_success.getStatusMessage());
			return ResponseEntity.status(HttpStatus.OK).body(message);
		} 
		else if (status == -1) {
			// Tenant not found, invalid tenantId
			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.USER_VALID_NOT_FOUND.getStatusCode(),
					HrManagementEnum.USER_VALID_NOT_FOUND.getStatusMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}

		ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.User_deleted_fail.getStatusCode(),
				HrManagementEnum.User_deleted_fail.getStatusMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	
}
	
	@PreAuthorize("@authService.hasPermission()")
	@GetMapping("roles")
	public ResponseEntity<?> getAllAvailableUsersRoles(){
		List<?> list = service.getAllAvailableRoles();
		if(list.isEmpty()) {
			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.NO_USERS_FOUND_LIST.getStatusCode(),
					HrManagementEnum.NO_USERS_FOUND_LIST.getStatusMessage());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
		
		}
		return ResponseEntity.ok(list) ;
		
	}
	
	@PreAuthorize("@authService.hasPermission()")
	@PutMapping("users/roles")
	public ResponseEntity<?> updateUserRole(@RequestParam String employeeUuid, @RequestParam String roleUuid) {
		int status = service.updateUserRole(employeeUuid,roleUuid);
	
		if (status > 0) {
			StatusMessageDto message = new StatusMessageDto(
					HrManagementEnum.USER_ROLE_UPDATED.getStatusCode(),
					HrManagementEnum.USER_ROLE_UPDATED.getStatusMessage());
			return ResponseEntity.status(HttpStatus.OK).body(message);
		} else if (status == -1) {
			// User not found, invalid tenantId
			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.USER_VALID_NOT_FOUND.getStatusCode(),
					HrManagementEnum.USER_VALID_NOT_FOUND.getStatusMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}

		ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.USER_ROLE_UPDATION_FAIL.getStatusCode(),
				HrManagementEnum.USER_ROLE_UPDATION_FAIL.getStatusMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	
	}
	
	
	}
	
	


