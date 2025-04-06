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
import com.isteer.entity.Tenants;
import com.isteer.enums.HrManagementEnum;
import com.isteer.service.HrManagementTenantService;

import jakarta.validation.Valid;

@RequestMapping("/hrManagement")
@RestController
public class HrManagementTenantController {

    private static final Logger logger = LogManager.getLogger(HrManagementTenantController.class);

	
	@Autowired
	HrManagementTenantService service;

	@PreAuthorize("@authService.hasPermission()")
	@PostMapping("tenant")
	public ResponseEntity<?> createTenant(@Valid @RequestBody Tenants tenant) {
        logger.info("Attempting to create tenant with name: {}", tenant.getTenantName());

		int status = service.createTenant(tenant);
		if (status > 0) {
            logger.info("Tenant with name '{}' created successfully.", tenant.getTenantName());

			StatusMessageDto message = new StatusMessageDto(HrManagementEnum.Tenant_created_message.getStatusCode(),
					HrManagementEnum.Tenant_created_message.getStatusMessage());
			return ResponseEntity.status(HttpStatus.OK).body(message);
		}
        logger.error("Failed to create tenant with name '{}'.", tenant.getTenantName());

		ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.Tenant_creation_failed.getStatusCode(),
				HrManagementEnum.Tenant_creation_failed.getStatusMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@PreAuthorize("@authService.hasPermission()")
	@GetMapping("tenant")
	public ResponseEntity<?> getAllTenants() {
        logger.info("Fetching all tenants.");

		List<?> tenants = service.getAllTenants();

		if (tenants.isEmpty()) {
            logger.warn("No tenants found.");

			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.No_list_of_tenansts.getStatusCode(),
					HrManagementEnum.No_list_of_tenansts.getStatusMessage());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
		}
        logger.info("Retrieved {} tenants.", tenants.size());

		return ResponseEntity.ok(tenants);
	}

	@PreAuthorize("@authService.hasPermission()")
	@PutMapping("tenant")
	public ResponseEntity<?> updateTenant(@RequestParam String tenantUuid, @Valid @RequestBody Tenants tenant) {
		tenant.setTenantUuid(tenantUuid);
        logger.info("Attempting to update tenant with UUID: {}", tenantUuid);
		int status = service.updateTenant(tenant);

		if (status > 0) {
            logger.info("Tenant with UUID '{}' updated successfully.", tenantUuid);

			StatusMessageDto message = new StatusMessageDto(
					HrManagementEnum.Tenant_updation_successfull.getStatusCode(),
					HrManagementEnum.Tenant_updation_successfull.getStatusMessage());
			return ResponseEntity.status(HttpStatus.OK).body(message);
		} else if (status == -1) {
            logger.warn("Tenant with UUID '{}' not found for update.", tenantUuid);

			// Tenant not found, invalid tenantId
			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.Tenant_valid_not_found.getStatusCode(),
					HrManagementEnum.Tenant_valid_not_found.getStatusMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}
        logger.error("Failed to update tenant with UUID '{}'.", tenantUuid);

		ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.Tenant_creation_failed.getStatusCode(),
				HrManagementEnum.Tenant_creation_failed.getStatusMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@PreAuthorize("@authService.hasPermission()")
	@DeleteMapping("tenant")
	public ResponseEntity<?> deleteTenant(@RequestParam String tenantUuid){
        logger.info("Attempting to delete tenant with UUID: {}", tenantUuid);

		int status = service.deleteTenant(tenantUuid);
		
		if (status > 0) {
            logger.info("Tenant with UUID '{}' deleted successfully.", tenantUuid);

			StatusMessageDto message = new StatusMessageDto(
					HrManagementEnum.Tenant_deletion.getStatusCode(),
					HrManagementEnum.Tenant_deletion.getStatusMessage());
			return ResponseEntity.status(HttpStatus.OK).body(message);
		} 
		else if (status == -1) {
            logger.warn("Tenant with UUID '{}' not found for deletion.", tenantUuid);
			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.Tenant_valid_not_found.getStatusCode(),
					HrManagementEnum.Tenant_valid_not_found.getStatusMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}
        logger.error("Failed to delete tenant with UUID '{}'.", tenantUuid);

		ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.TENANT_FAILED_DELETION.getStatusCode(),
				HrManagementEnum.TENANT_FAILED_DELETION.getStatusMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	
}
	
}