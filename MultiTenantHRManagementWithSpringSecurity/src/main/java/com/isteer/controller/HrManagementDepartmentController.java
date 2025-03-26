package com.isteer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.isteer.entity.Employee;
import com.isteer.enums.HrManagementEnum;
import com.isteer.service.HrManagementDepartmentService;

import jakarta.validation.Valid;




@RequestMapping("/hrManagement")
@RestController
public class HrManagementDepartmentController {

	@Autowired
	HrManagementDepartmentService service;
	
		
	@PostMapping("departments")
	public ResponseEntity<?> addDepartment(@RequestParam String tenantId ,@Valid @RequestBody Departments departments) {
		  departments.setTenantId(tenantId);
	    int status = service.addDepartment(tenantId, departments);

	    if (status > 0) {
	        // Department created successfully
	        StatusMessageDto message = new StatusMessageDto(
	                HrManagementEnum.Department_created_message.getStatusCode(),
	                HrManagementEnum.Department_created_message.getStatusMessage());
	        return ResponseEntity.status(HttpStatus.OK).body(message);
	    } else if (status == -1) {
	        // Tenant not found, invalid tenantId
	        ErrorMessageDto error = new ErrorMessageDto(
	                HrManagementEnum.Tenant_valid_not_found.getStatusCode(),
	                HrManagementEnum.Tenant_valid_not_found.getStatusMessage());
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	    } else if (status == -2) {
	        // Department already exists
	        ErrorMessageDto error = new ErrorMessageDto(
	                HrManagementEnum.DUPLICATE_KEY_EXCEPTION.getStatusCode(),
	                HrManagementEnum.DUPLICATE_KEY_EXCEPTION.getStatusMessage());
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	    }

	    // Department creation failed due to some unknown error
	    ErrorMessageDto error = new ErrorMessageDto(
	            HrManagementEnum.DEPARTMENT_CREATION_FAILED.getStatusCode(),
	            HrManagementEnum.DEPARTMENT_CREATION_FAILED.getStatusMessage());
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@GetMapping("/departments")
	public ResponseEntity<?> getAllDepartments() {
	List<?> list = service.getAllDepartments();
	
	if (list.isEmpty()) {
		ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.No_list_of_tenansts.getStatusCode(),
				HrManagementEnum.No_list_of_tenansts.getStatusMessage());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
	}

	return ResponseEntity.ok(list);
	}
	
	
	@PutMapping("departments")
	public ResponseEntity<?> updateDepartMent(@Valid @RequestParam String departmentId, @RequestBody Departments department) {
	     department.setDepartmentId(departmentId);
		int status = service.updateTenant(department);
		
		if (status > 0) {
			StatusMessageDto message = new StatusMessageDto(
					HrManagementEnum.DEPARTMENT_UPDATION_SUCCESSFULL.getStatusCode(),
					HrManagementEnum.DEPARTMENT_UPDATION_SUCCESSFULL.getStatusMessage());
			return ResponseEntity.status(HttpStatus.OK).body(message);
		} else if (status == -1) {
			// Tenant not found, invalid tenantId
			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.DEPARTMENT_VALID_NOT_FOUND.getStatusCode(),
					HrManagementEnum.DEPARTMENT_VALID_NOT_FOUND.getStatusMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}

		ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.DEPARTMENT_CREATION_FAILED.getStatusCode(),
				HrManagementEnum.DEPARTMENT_CREATION_FAILED.getStatusMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	
	}
	
	@DeleteMapping("department")
	public ResponseEntity<?> deleteDepartment(@RequestParam String departmentId){
		
		int status = service.deleteDepartment(departmentId);
		
		if (status > 0) {
			StatusMessageDto message = new StatusMessageDto(
					HrManagementEnum.DEPARTMENT_DELETION.getStatusCode(),
					HrManagementEnum.DEPARTMENT_DELETION.getStatusMessage());
			return ResponseEntity.status(HttpStatus.OK).body(message);
		} 
		else if (status == -1) {
			// Tenant not found, invalid tenantId
			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.DEPARTMENT_VALID_NOT_FOUND.getStatusCode(),
					HrManagementEnum.DEPARTMENT_VALID_NOT_FOUND.getStatusMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}

		ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.DEPARTMENT_DELETION_FAILED.getStatusCode(),
				HrManagementEnum.DEPARTMENT_DELETION_FAILED.getStatusMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	
}
	
	@GetMapping("department/employee")
	public ResponseEntity<?> getEmployeesByDepartment(@RequestParam String departmentId) {
		List<?> list = service.getAllEmployeesByDepartmentId(departmentId);
		if (list.isEmpty()) {
			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.No_list_of_tenansts.getStatusCode(),
					HrManagementEnum.No_list_of_tenansts.getStatusMessage());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
		}

		return ResponseEntity.ok(list);
	}
	

	
}
