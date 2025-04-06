package com.isteer.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.isteer.entity.Departments;
import com.isteer.enums.HrManagementEnum;
import com.isteer.service.HrManagementDepartmentService;

import jakarta.validation.Valid;




@RequestMapping("/hrManagement")
@RestController
public class HrManagementDepartmentController {

	@Autowired
	HrManagementDepartmentService service;
	
    private static final Logger logging = LogManager.getLogger(HrManagementDepartmentController.class);

	
	@PreAuthorize("@authService.hasPermission()")	
	@PostMapping("departments")
	public ResponseEntity<?> addDepartment(@RequestParam String tenantUuid ,@Valid @RequestBody Departments departments) {
        logging.info("Attempting to add department for tenant: {}", tenantUuid);
		  departments.setTenantUuid(tenantUuid);
	    int status = service.addDepartment(tenantUuid, departments);

	    if (status > 0) {
            logging.info("Department created successfully for tenant: {}", tenantUuid);
	        StatusMessageDto message = new StatusMessageDto(
	                HrManagementEnum.Department_created_message.getStatusCode(),
	                HrManagementEnum.Department_created_message.getStatusMessage());
	        return ResponseEntity.status(HttpStatus.OK).body(message);
	    } else if (status == -1) {
            logging.warn("Tenant not found for UUID: {}", tenantUuid);
	        ErrorMessageDto error = new ErrorMessageDto(
	                HrManagementEnum.Tenant_valid_not_found.getStatusCode(),
	                HrManagementEnum.Tenant_valid_not_found.getStatusMessage());
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	    } else if (status == -2) {
            logging.warn("Department already exists for tenant: {}", tenantUuid);
	        ErrorMessageDto error = new ErrorMessageDto(
	                HrManagementEnum.DUPLICATE_KEY_EXCEPTION.getStatusCode(),
	                HrManagementEnum.DUPLICATE_KEY_EXCEPTION.getStatusMessage());
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	    }

        logging.error("Department creation failed for tenant: {}", tenantUuid);
	    ErrorMessageDto error = new ErrorMessageDto(
	            HrManagementEnum.DEPARTMENT_CREATION_FAILED.getStatusCode(),
	            HrManagementEnum.DEPARTMENT_CREATION_FAILED.getStatusMessage());
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@PreAuthorize("@authService.hasPermission()")
	@GetMapping("/departments")
	public ResponseEntity<?> getAllDepartments(String tenantUuid) {
        logging.info("Fetching all departments for tenant: {}", tenantUuid);
	List<?> list = service.getAllDepartments(tenantUuid);
	
	if (list.isEmpty()) {
        logging.warn("No departments found for tenant: {}", tenantUuid);

		ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.No_list_of_tenansts.getStatusCode(),
				HrManagementEnum.No_list_of_tenansts.getStatusMessage());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
	}
    logging.info("Successfully retrieved {} departments for tenant: {}", list.size(), tenantUuid);

	return ResponseEntity.ok(list);
	}
	
	@PreAuthorize("@authService.hasPermission()")
	@PutMapping("departments")
	public ResponseEntity<?> updateDepartMent(@Valid @RequestParam String departmentUuid, @RequestBody Departments department) {
		 logging.info("Attempting to update department with UUID: {}", departmentUuid); 
		department.setDepartmentUuid(departmentUuid);
		int status = service.updateTenant(department);
		
		if (status > 0) {
            logging.info("Department updated successfully for UUID: {}", departmentUuid);
			StatusMessageDto message = new StatusMessageDto(
					HrManagementEnum.DEPARTMENT_UPDATION_SUCCESSFULL.getStatusCode(),
					HrManagementEnum.DEPARTMENT_UPDATION_SUCCESSFULL.getStatusMessage());
			return ResponseEntity.status(HttpStatus.OK).body(message);
		} else if (status == -1) {
            logging.warn("Department not found for UUID: {}", departmentUuid);
			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.DEPARTMENT_VALID_NOT_FOUND.getStatusCode(),
					HrManagementEnum.DEPARTMENT_VALID_NOT_FOUND.getStatusMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}
        logging.error("Department update failed for UUID: {}", departmentUuid);
		ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.DEPARTMENT_CREATION_FAILED.getStatusCode(),
				HrManagementEnum.DEPARTMENT_CREATION_FAILED.getStatusMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	
	}
	
	@PreAuthorize("@authService.hasPermission()")
	@DeleteMapping("department")
	public ResponseEntity<?> deleteDepartment(@RequestParam String departmentUuid){
        logging.info("Attempting to delete department with UUID: {}", departmentUuid);

		int status = service.deleteDepartment(departmentUuid);
		
		if (status > 0) {
            logging.info("Department deleted successfully with UUID: {}", departmentUuid);

			StatusMessageDto message = new StatusMessageDto(
					HrManagementEnum.DEPARTMENT_DELETION.getStatusCode(),
					HrManagementEnum.DEPARTMENT_DELETION.getStatusMessage());
			return ResponseEntity.status(HttpStatus.OK).body(message);
		} 
		else if (status == -1) {
            logging.warn("Department not found for UUID: {}", departmentUuid);
			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.DEPARTMENT_VALID_NOT_FOUND.getStatusCode(),
					HrManagementEnum.DEPARTMENT_VALID_NOT_FOUND.getStatusMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}
        logging.error("Department deletion failed for UUID: {}", departmentUuid);

		ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.DEPARTMENT_DELETION_FAILED.getStatusCode(),
				HrManagementEnum.DEPARTMENT_DELETION_FAILED.getStatusMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	
}
	@PreAuthorize("@authService.hasPermission()")
	@GetMapping("department/employee")
	public ResponseEntity<?> getEmployeesByDepartment(@RequestParam String departmentUuid) {
        logging.info("Fetching employees for department UUID: {}", departmentUuid);

		List<?> list = service.getAllEmployeesByDepartmentId(departmentUuid);
		if (list.isEmpty()) {
            logging.warn("No employees found for department UUID: {}", departmentUuid);
			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.No_list_of_tenansts.getStatusCode(),
					HrManagementEnum.No_list_of_tenansts.getStatusMessage());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
		}
        logging.info("Successfully retrieved {} employees for department UUID: {}", list.size(), departmentUuid);

		return ResponseEntity.ok(list);
	}
	

	
}
