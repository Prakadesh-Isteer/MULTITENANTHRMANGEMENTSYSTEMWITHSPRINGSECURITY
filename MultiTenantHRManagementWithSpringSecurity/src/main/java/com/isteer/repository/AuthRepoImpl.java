package com.isteer.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import com.isteer.dto.HttpMethodRoleRights;
import com.isteer.dto.RequestPermisionDto;
import com.isteer.util.HttpMethodPermissionRowmapper;
import com.isteer.util.UrlPermissionRowMapper;

@Component
public class AuthRepoImpl {

	
	@Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public List<RequestPermisionDto> findUrl(String url){
		String findUrl = "SELECT erm.endpoint_id, erm.role_id " +
                "FROM endpoints_role_mapping erm " +
                "JOIN endpoints e ON erm.endpoint_id = e.endpoint_uuid " +
                "WHERE e.endpoint_url = :url";
		SqlParameterSource param = new MapSqlParameterSource()
				.addValue("url", url);
		List<RequestPermisionDto> accessible = namedParameterJdbcTemplate.query(findUrl, param, new UrlPermissionRowMapper());
		return accessible;
	}
	
	public List<HttpMethodRoleRights> findHttpMethod(String httpMethod){
		String findHttp = "SELECT hmm.http_id, hmm.role_id " +
                "FROM http_method_mapping hmm " +
                "JOIN http_methods hm ON hmm.http_id = hm.http_method_uuid " +
                "WHERE hm.http_method = :httpMethod";
		SqlParameterSource param = new MapSqlParameterSource()
				.addValue("httpMethod", httpMethod);
		List<HttpMethodRoleRights> roles = namedParameterJdbcTemplate.query(findHttp, param, new HttpMethodPermissionRowmapper());
		return roles;
	}

    // Method to add a new endpoint and its role mapping using role_uuid
    public int addEndpointWithRoleMapping(String endpointUrl, String roleUuid) {
        // Step 1: Insert the new endpoint into the endpoints table
        String insertEndpointSQL = "INSERT INTO endpoints (endpoint_url, endpoint_uuid) VALUES (:endpointUrl, :endpointId)";
        
        String endpointUuid = java.util.UUID.randomUUID().toString();  // Generate a new UUID for the endpoint

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("endpointUrl", endpointUrl);
        params.addValue("endpointId", endpointUuid);
        
        // Insert the new endpoint
        namedParameterJdbcTemplate.update(insertEndpointSQL, params);
        

        // Step 2: Insert the mapping into the endpoints_role_mapping table
        String insertMappingSQL = "INSERT INTO endpoints_role_mapping (endpoint_id, role_id) VALUES (:endpointUuid, :roleUuid)";
        
        MapSqlParameterSource mappingParams = new MapSqlParameterSource();
        mappingParams.addValue("endpointUuid", endpointUuid);
        mappingParams.addValue("roleUuid", roleUuid);
        
        // Insert the mapping
        return namedParameterJdbcTemplate.update(insertMappingSQL, mappingParams);
    }
    
    
    
    public int addHttpMethodMapping(String httpMethod, String roleUuid) {
    	
    	 // Step 1: Insert the new endpoint into the endpoints table
        String insertMethodSQL = "INSERT INTO http_methods (http_method, http_method_uuid) VALUES (:httpMethod, :httpMethodUuid)";
        
        String httpMethodUuid = java.util.UUID.randomUUID().toString();  // Generate a new UUID for the endpoint
        System.out.println(httpMethodUuid);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("httpMethod", httpMethod);
        params.addValue("httpMethodUuid", httpMethodUuid);
        
        // Insert the new endpoint
        namedParameterJdbcTemplate.update(insertMethodSQL, params);

        // Step 2: Insert the mapping into the endpoints_role_mapping table

        String insertMappingSQL = "INSERT INTO http_method_mapping (http_id, role_id) VALUES (:httpMethodUuid, :roleUuid)";

        params.addValue("httpMethodUuid",httpMethodUuid);
        params.addValue("roleUuid", roleUuid);

        // Insert the mapping into the http_method_mapping table
        int sts = namedParameterJdbcTemplate.update(insertMappingSQL, params);
        return sts;
    }

   
//    // Get Role UUID by role_id (This is an optional method if you want to validate or check role_uuid before inserting)
//    public String getRoleIdByUuid(String roleUuid) {
//        String getRoleIdSQL = "SELECT role_uuid FROM roles WHERE role_uuid = :roleUuid LIMIT 1";
//        
//        MapSqlParameterSource params = new MapSqlParameterSource();
//        params.addValue("roleUuid", roleUuid);
//
//        String roleId = namedParameterJdbcTemplate.queryForObject(getRoleIdSQL, params, String.class);
//
//        if (roleId == null) {
//            throw new RoleIdNullException(HrManagementEnum.Role_id_null);
//        }
//        
//        return roleId;
//    }
//    
  
}
