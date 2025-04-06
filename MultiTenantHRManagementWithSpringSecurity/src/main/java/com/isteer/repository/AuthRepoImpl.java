package com.isteer.repository;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import com.isteer.dto.HttpMethodRoleRights;
import com.isteer.dto.RequestPermisionDto;
import com.isteer.enums.HrManagementEnum;
import com.isteer.exception.EndpointNullException;
import com.isteer.exception.RoleIdNotFoundException;
import com.isteer.exception.RoleIdNullException;
import com.isteer.repository.dao.AuthRepoDao;
import com.isteer.util.HttpMethodPermissionRowmapper;
import com.isteer.util.UrlPermissionRowMapper;

@Component
public class AuthRepoImpl implements AuthRepoDao {

	private static final Logger logger = LogManager.getLogger(AuthRepoImpl.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public List<RequestPermisionDto> findUrl(String url) {
		logger.info("Searching for URL mappings for URL: {}", url);

		String findUrl = "SELECT erm.endpoint_id, erm.role_id " + "FROM endpoints_role_mapping erm "
				+ "JOIN endpoints e ON erm.endpoint_id = e.endpoint_uuid " + "WHERE e.endpoint_url = :url";
		SqlParameterSource param = new MapSqlParameterSource().addValue("url", url);
		List<RequestPermisionDto> accessible = namedParameterJdbcTemplate.query(findUrl, param,
				new UrlPermissionRowMapper());
		logger.info("Found {} mappings for URL: {}", accessible.size(), url);

		return accessible;
	}

	@Override
	public List<HttpMethodRoleRights> findHttpMethod(String httpMethod) {
		logger.info("Searching for HTTP method mappings for HTTP method: {}", httpMethod);

		String findHttp = "SELECT hmm.http_id, hmm.role_id " + "FROM http_method_mapping hmm "
				+ "JOIN http_methods hm ON hmm.http_id = hm.http_method_uuid " + "WHERE hm.http_method = :httpMethod";
		SqlParameterSource param = new MapSqlParameterSource().addValue("httpMethod", httpMethod);
		List<HttpMethodRoleRights> roles = namedParameterJdbcTemplate.query(findHttp, param,
				new HttpMethodPermissionRowmapper());
		logger.info("Found {} mappings for HTTP method: {}", roles.size(), httpMethod);

		return roles;
	}

	@Override
	public int addEndpointWithRoleMapping(String endpointUrl, String roleUuid) {
		logger.info("Adding endpoint with URL: {} and role UUID: {}", endpointUrl, roleUuid);

		if (roleUuid.trim().isBlank()) {
			logger.error("Role UUID is blank or null for endpoint: {}", endpointUrl);

			throw new RoleIdNullException(HrManagementEnum.Role_id_null);
		}

		if (endpointUrl.trim().isBlank()) {
			logger.error("Endpoint URL is blank or null for role UUID: {}", roleUuid);
			throw new EndpointNullException(HrManagementEnum.End_point_null);
		}

		// Step 1: Insert the new endpoint into the endpoints table
		String insertEndpointSQL = "INSERT INTO endpoints (endpoint_url, endpoint_uuid) VALUES (:endpointUrl, :endpointId)";

		String endpointUuid = java.util.UUID.randomUUID().toString(); // Generate a new UUID for the endpoint

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("endpointUrl", endpointUrl);
		params.addValue("endpointId", endpointUuid);

		// Insert the new endpoint
		namedParameterJdbcTemplate.update(insertEndpointSQL, params);
		// Step 2: Check if the role exists before inserting mapping
		String checkRoleSQL = "SELECT COUNT(*) FROM roles WHERE role_uuid = :roleUuid";
		MapSqlParameterSource checkParams = new MapSqlParameterSource();
		checkParams.addValue("roleUuid", roleUuid);

		int roleCount = namedParameterJdbcTemplate.queryForObject(checkRoleSQL, checkParams, Integer.class);
		if (roleCount == 0) {
			throw new RoleIdNotFoundException(HrManagementEnum.Illegal_Argumnet_role);
		}

		// Step 2: Insert the mapping into the endpoints_role_mapping table
		String insertMappingSQL = "INSERT INTO endpoints_role_mapping (endpoint_id, role_id) VALUES (:endpointUuid, :roleUuid)";

		MapSqlParameterSource mappingParams = new MapSqlParameterSource();
		mappingParams.addValue("endpointUuid", endpointUuid);
		mappingParams.addValue("roleUuid", roleUuid);
		logger.info("Endpoint and role mapping successfully added for endpoint: {} and role: {}", endpointUrl,
				roleUuid);

		// Insert the mapping
		return namedParameterJdbcTemplate.update(insertMappingSQL, mappingParams);

	}

	@Override
	public int addHttpMethodMapping(String httpMethod, String roleUuid) {
		logger.info("Adding HTTP method: {} with role UUID: {}", httpMethod, roleUuid);

		if (roleUuid.trim().isBlank()) {
			logger.error("Role UUID is blank or null for endpoint: {}", httpMethod);

			throw new RoleIdNullException(HrManagementEnum.Role_id_null);
		}
		// Step 1: Insert the new endpoint into the endpoints table
		String insertMethodSQL = "INSERT INTO http_methods (http_method, http_method_uuid) VALUES (:httpMethod, :httpMethodUuid)";

		String httpMethodUuid = java.util.UUID.randomUUID().toString(); // Generate a new UUID for the endpoint
		System.out.println(httpMethodUuid);
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("httpMethod", httpMethod);
		params.addValue("httpMethodUuid", httpMethodUuid);
		logger.debug("Inserting HTTP method: {}", httpMethod);

		namedParameterJdbcTemplate.update(insertMethodSQL, params);
		// Step 2: Check if the role exists before inserting mapping
		String checkRoleSQL = "SELECT COUNT(*) FROM roles WHERE role_uuid = :roleUuid";
		MapSqlParameterSource checkParams = new MapSqlParameterSource();
		checkParams.addValue("roleUuid", roleUuid);

		int roleCount = namedParameterJdbcTemplate.queryForObject(checkRoleSQL, checkParams, Integer.class);
		if (roleCount == 0) {
			throw new RoleIdNotFoundException(HrManagementEnum.Illegal_Argumnet_role);
		}

		String insertMappingSQL = "INSERT INTO http_method_mapping (http_id, role_id) VALUES (:httpMethodUuid, :roleUuid)";

		params.addValue("httpMethodUuid", httpMethodUuid);
		params.addValue("roleUuid", roleUuid);
		logger.debug("Inserting mapping for HTTP method UUID: {} and role UUID: {}", httpMethodUuid, roleUuid);

		int sts = namedParameterJdbcTemplate.update(insertMappingSQL, params);
		logger.info("Successfully added HTTP method mapping: {} -> {}", httpMethod, roleUuid);

		return sts;
	}

}
