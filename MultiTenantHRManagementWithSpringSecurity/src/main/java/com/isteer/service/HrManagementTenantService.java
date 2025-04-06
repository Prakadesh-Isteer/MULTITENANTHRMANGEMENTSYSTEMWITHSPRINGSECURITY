package com.isteer.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isteer.entity.Tenants;
import com.isteer.repository.TenantRepoDaoImpl;

@Service
public class HrManagementTenantService {
	
    private static final Logger logger = LogManager.getLogger(HrManagementTenantService.class);

	
	@Autowired
	TenantRepoDaoImpl repo;
	
	public int createTenant(Tenants tenants) {
        logger.info("Creating new tenant with name: {}", tenants.getTenantName());

		return repo.addTenant(tenants);
	}
	
	public List<Tenants> getAllTenants(){
        logger.info("Fetching all tenants.");

		return repo.getAllTenants();
		
	}
	
	public int updateTenant(Tenants tenant) {
        logger.info("Updating tenant with ID: {}", tenant.getTenantUuid());

		return repo.updateTenant(tenant);  
		
	}
	
	public int deleteTenant(String tenantId) {
        logger.info("Deleting tenant with ID: {}", tenantId);

		return repo.deleteTenant(tenantId);
	}

}
