package com.isteer.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.isteer.dto.RequestPermisionDto;

public class UrlPermissionRowMapper implements RowMapper<RequestPermisionDto> {

	@Override
	public RequestPermisionDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		RequestPermisionDto permission = new RequestPermisionDto();
		permission.setRoleId(rs.getString("role_id"));
		permission.setUrlPattern(rs.getString("endpoint_id"));
		return permission;
	}

}
