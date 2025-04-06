package com.isteer.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.isteer.dto.ErrorMessageDto;
import com.isteer.dto.StatusMessageDto;
import com.isteer.dto.UserDetailsDto;
import com.isteer.entity.Employee;
import com.isteer.entity.Roles;
import com.isteer.enums.HrManagementEnum;
import com.isteer.service.HrManagementEmployeeService;

import jakarta.validation.Valid;




@RestController
@RequestMapping("/hrManagement")
public class HrManagementEmployeeController {
	
    private static final Logger logger = LogManager.getLogger(HrManagementEmployeeController.class);

	
	@Autowired
	HrManagementEmployeeService service;
	
	@PreAuthorize("@authService.hasPermission()")
	@PostMapping("user")
	public ResponseEntity<?> registerUser(@RequestParam String departmentUuid, @Valid @RequestBody UserDetailsDto details) {
        logger.info("Attempting to register user in department: {}", departmentUuid);

		int status = service.registerUser(details, departmentUuid);
		if(status > 0) {
            logger.info("User created successfully for department: {}", departmentUuid);

			 StatusMessageDto message = new StatusMessageDto(
		                HrManagementEnum.USER_CREATED_SUCCESS.getStatusCode(),
		                HrManagementEnum.USER_CREATED_SUCCESS.getStatusMessage());
		        return ResponseEntity.status(HttpStatus.OK).body(message);
		}
        logger.error("User creation failed for department: {}", departmentUuid);
		  ErrorMessageDto error = new ErrorMessageDto(
		            HrManagementEnum.USER_FAILED_CREATION.getStatusCode(),
		            HrManagementEnum.USER_FAILED_CREATION.getStatusMessage());
		    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@PreAuthorize("@authService.hasPermission()")
	@PostMapping("addRole")
	public ResponseEntity<?> addRole(@Valid @RequestBody Roles role) {
        logger.info("Attempting to add role: {}", role.getRoleName());

		int status = service.addRole(role);
		if(status > 0) {
            logger.info("Role added successfully: {}", role.getRoleName());

			 StatusMessageDto message = new StatusMessageDto(
		                HrManagementEnum.Role_added.getStatusCode(),
		                HrManagementEnum.Role_added.getStatusMessage());
		        return ResponseEntity.status(HttpStatus.OK).body(message);
		}
		   logger.error("Failed to add role: {}", role.getRoleName());
		  ErrorMessageDto error = new ErrorMessageDto(
		            HrManagementEnum.Role_not_added.getStatusCode(),
		            HrManagementEnum.Role_not_added.getStatusMessage());
		    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		
	}
	@PreAuthorize("@authService.hasPermission()")
	@GetMapping("users")
	public ResponseEntity<?> getAllUsers(){
        logger.info("Fetching all users");

		List<?> list = service.getAllUsers();
		if(list.isEmpty()) {
            logger.warn("No users found.");

			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.NO_USERS_FOUND_LIST.getStatusCode(),
					HrManagementEnum.NO_USERS_FOUND_LIST.getStatusMessage());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
		
		}
        logger.info("Successfully retrieved {} users.", list.size());
		return ResponseEntity.ok(list) ;
		
	}
	
	
	@PreAuthorize("@authService.hasPermission()")
	@GetMapping("/user")
	public ResponseEntity<?> getUsersByEmployeeUuid(@RequestParam String employeeUuid) {
        logger.info("Fetching user by employee UUID: {}", employeeUuid);

		List<Employee> single = service.getAllUsersByEmployeeUuid(employeeUuid);
		if(single.isEmpty()) {
            logger.warn("No users found for employee UUID: {}", employeeUuid);

			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.NO_USERS_FOUND_LIST.getStatusCode(),
					HrManagementEnum.NO_USERS_FOUND_LIST.getStatusMessage());
	        logger.info("Successfully retrieved user for employee UUID: {}", employeeUuid);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
		
		}
		return ResponseEntity.ok(single);
	}
	
	@PreAuthorize("@authService.hasPermission()")
	@PutMapping("user")
	public ResponseEntity<?> updateUser(@RequestParam String employeeUuid, @Valid @RequestBody UserDetailsDto details) {
        logger.info("Attempting to update user with employee UUID: {}", employeeUuid);

		details.setEmployeeUuid(employeeUuid);
		int status = service.updateUser(details);
	
		if (status > 0) {
            logger.info("User updated successfully for employee UUID: {}", employeeUuid);
			StatusMessageDto message = new StatusMessageDto(
					HrManagementEnum.USER_UPDATED_SUCCESS.getStatusCode(),
					HrManagementEnum.USER_UPDATED_SUCCESS.getStatusMessage());
			return ResponseEntity.status(HttpStatus.OK).body(message);
		} else if (status == -1) {
            logger.warn("User not found for employee UUID: {}", employeeUuid);
			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.USER_VALID_NOT_FOUND.getStatusCode(),
					HrManagementEnum.USER_VALID_NOT_FOUND.getStatusMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}
        logger.error("User update failed for employee UUID: {}", employeeUuid);

		ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.USER_UPDATE_FAIL.getStatusCode(),
				HrManagementEnum.USER_UPDATE_FAIL.getStatusMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	
	}
	
	@PreAuthorize("@authService.hasPermission()")
	@DeleteMapping("user")
	public ResponseEntity<?> deleteEmployee(@RequestParam String employeeUuid){
        logger.info("Attempting to delete employee with UUID: {}", employeeUuid);

		int status = service.deleteEmployee(employeeUuid);
		
		if (status > 0) {
            logger.info("Employee deleted successfully for UUID: {}", employeeUuid);
			StatusMessageDto message = new StatusMessageDto(
					HrManagementEnum.User_deleted_success.getStatusCode(),
					HrManagementEnum.User_deleted_success.getStatusMessage());
			return ResponseEntity.status(HttpStatus.OK).body(message);
		} 
		else if (status == -1) {
            logger.warn("User not found for employee UUID: {}", employeeUuid);
			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.USER_VALID_NOT_FOUND.getStatusCode(),
					HrManagementEnum.USER_VALID_NOT_FOUND.getStatusMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}
        logger.error("Employee deletion failed for UUID: {}", employeeUuid);

		ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.User_deleted_fail.getStatusCode(),
				HrManagementEnum.User_deleted_fail.getStatusMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	
}
	
	@PreAuthorize("@authService.hasPermission()")
	@GetMapping("roles")
	public ResponseEntity<?> getAllAvailableUsersRoles(){
        logger.info("Fetching all available roles");

		List<?> list = service.getAllAvailableRoles();
		if(list.isEmpty()) {
            logger.warn("No roles found.");

			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.NO_USERS_FOUND_LIST.getStatusCode(),
					HrManagementEnum.NO_USERS_FOUND_LIST.getStatusMessage());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
		
		}
        logger.info("Successfully retrieved {} roles.", list.size());

		return ResponseEntity.ok(list) ;
		
	}
	
	@PreAuthorize("@authService.hasPermission()")
	@PutMapping("users/roles")
	public ResponseEntity<?> updateUserRole(@RequestParam String employeeUuid, @RequestParam String roleUuid) {
        logger.info("Attempting to update role for employee UUID: {} to role UUID: {}", employeeUuid, roleUuid);

		int status = service.updateUserRole(employeeUuid,roleUuid);
	
		if (status > 0) {
            logger.info("User role updated successfully for employee UUID: {}", employeeUuid);

			StatusMessageDto message = new StatusMessageDto(
					HrManagementEnum.USER_ROLE_UPDATED.getStatusCode(),
					HrManagementEnum.USER_ROLE_UPDATED.getStatusMessage());
			return ResponseEntity.status(HttpStatus.OK).body(message);
		} else if (status == -1) {
            logger.warn("User not found for employee UUID: {}", employeeUuid);
			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.USER_VALID_NOT_FOUND.getStatusCode(),
					HrManagementEnum.USER_VALID_NOT_FOUND.getStatusMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}
        logger.error("User role update failed for employee UUID: {}", employeeUuid);

		ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.USER_ROLE_UPDATION_FAIL.getStatusCode(),
				HrManagementEnum.USER_ROLE_UPDATION_FAIL.getStatusMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	
	}
	
	
	}
	
	


