package com.isteer.repository.dao;

import java.util.List;


import com.isteer.entity.Tenants;

public interface TenantRepoDao {
	
	public int addTenant(Tenants tenants);
	public List<Tenants> getAllTenants();
	public int updateTenant(Tenants tenant);
	public int deleteTenant(String tenantId);


}
