package com.isteer.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.isteer.entity.Tenants;
import com.isteer.enums.HrManagementEnum;
import com.isteer.exception.TenantIdNullException;
import com.isteer.repository.dao.TenantRepoDao;
import com.isteer.util.TenantRowMapper;

@Component
public class TenantRepoDaoImpl implements TenantRepoDao{
	
	@Autowired
	NamedParameterJdbcTemplate template;
	
	@Transactional
	@Override
	public int addTenant(Tenants tenants) {
		UUID uuid = UUID.randomUUID();
        String tenantUuid = uuid.toString();
		String insertTenant = "INSERT INTO tenants (tenant_uuid, tenant_name, address, contact_email, contact_phone, tenant_country, tenant_state, tenant_city) VALUES (:tenantId, :tenantName, :address, :email, :phone, :tenantCountry, :tenantState, :tenantCity)";
		SqlParameterSource param = new MapSqlParameterSource()
                .addValue("tenantId", tenantUuid)
				.addValue("tenantName", tenants.getTenantName())
				.addValue("address", tenants.getAddress() )
				.addValue("email", tenants.getEmail() )
				.addValue("phone", tenants.getPhoneNumber())
				.addValue("tenantCountry", tenants.getCountry())
				.addValue("tenantState", tenants.getState())
				.addValue("tenantCity", tenants.getCity());
		
		return template.update(insertTenant, param);
		
	}

	@Transactional
	@Override
	public List<Tenants> getAllTenants() {
		String sql = "SELECT tenant_uuid, tenant_name, address, contact_email, contact_phone, tenant_country, tenant_state, tenant_city FROM tenants WHERE tenant_status = :status";
		SqlParameterSource param = new MapSqlParameterSource()
				.addValue("status", 1);
		return template.query(sql, param, new TenantRowMapper());
	}

	@Transactional
	// Method to check if a tenant exists by tenantId
	public Optional<Tenants> findById(String tenantId) {
        String sql = "SELECT tenant_uuid, tenant_name, address, contact_email, contact_phone, tenant_country, tenant_state, tenant_city FROM tenants WHERE tenant_uuid = :tenantId";
        SqlParameterSource param = new MapSqlParameterSource().addValue("tenantId", tenantId);

        try {
            Tenants tenant = template.queryForObject(sql, param, new TenantRowMapper());
            return Optional.ofNullable(tenant);
        } catch (EmptyResultDataAccessException e) {
            // Return an empty Optional if no tenant found
            return Optional.empty();
        }
    }

	@Transactional
    // Method to update the tenant
	@Override
    public int updateTenant(Tenants tenant) {
        // Check if tenant exists before trying to update
        Optional<Tenants> existingTenant = findById(tenant.getTenantId());

        if (!existingTenant.isPresent()) {
            // If the tenant does not exist, return -1 to indicate failure
            return -1;
        }

        // SQL query for updating the tenant details
        String sql = "UPDATE tenants SET tenant_name = :tenantName, address = :address, " +
                     "contact_email = :email, contact_phone = :phone, tenant_country = :tenantCountry, " +
                     "tenant_state = :tenantState, tenant_city = :tenantCity WHERE tenant_uuid = :tenantId";

        SqlParameterSource param = new MapSqlParameterSource()
            .addValue("tenantId", tenant.getTenantId())
            .addValue("tenantName", tenant.getTenantName())
            .addValue("address", tenant.getAddress())
            .addValue("email", tenant.getEmail())
            .addValue("phone", tenant.getPhoneNumber())
            .addValue("tenantCountry", tenant.getCountry())
            .addValue("tenantState", tenant.getState())
            .addValue("tenantCity", tenant.getCity());

        try {
            // Execute the update query
            return template.update(sql, param);
        } catch (DataAccessException e) {
                 e.printStackTrace();
            // Log any database access errors and return 0 or throw a custom exception
//            log.error("Error updating tenant in the database: {}", e.getMessage());
        	System.out.println("tenant update");
            return 0;  // Indicating failure to update
        }
    }
 
	@Transactional
    @Override
    public int deleteTenant(String tenantId) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new TenantIdNullException(HrManagementEnum.Tenant_id_null);
        }

        try {
            // Check if tenant exists by selecting the tenant_uuid column
            String checkTenantExistsQuery = "SELECT tenant_uuid FROM tenants WHERE tenant_uuid = :tenantId  AND tenant_status = :status";
            MapSqlParameterSource param = new MapSqlParameterSource();
            param.addValue("status", 1);
            param.addValue("tenantId", tenantId);

            // Execute query to check if tenant exists
            List<String> tenantUuids = template.queryForList(checkTenantExistsQuery, param, String.class);

            // If the tenant UUID doesn't exist in the result list, return -1 (tenant not found)
            if (tenantUuids.isEmpty()) {
                return -1; // Tenant not found
            }

            // Soft delete query
            String softDelete = "UPDATE tenants SET tenant_status = :status WHERE tenant_uuid = :tenantId";
            param.addValue("status", 0);  // Assuming 0 represents deleted status
            
            // Perform the update (soft delete)
            return template.update(softDelete, param);
            
        } catch (Exception e) {
            // Log exception here (optional), rethrow or handle the exception
            return 0;  // Indicates failure
        }
    }


	
}
