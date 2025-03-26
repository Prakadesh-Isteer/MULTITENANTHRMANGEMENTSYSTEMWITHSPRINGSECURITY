package com.isteer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isteer.entity.Tenants;
import com.isteer.repository.TenantRepoDaoImpl;

@Service
public class HrManagementTenantService {
	
	@Autowired
	TenantRepoDaoImpl repo;
	
	public int createTenant(Tenants tenants) {
		return repo.addTenant(tenants);
	}
	
	public List<Tenants> getAllTenants(){
		
		return repo.getAllTenants();
		
	}
	
	public int updateTenant(Tenants tenant) {
		return repo.updateTenant(tenant);  
		
	}
	
	public int deleteTenant(String tenantId) {
		return repo.deleteTenant(tenantId);
	}

}
