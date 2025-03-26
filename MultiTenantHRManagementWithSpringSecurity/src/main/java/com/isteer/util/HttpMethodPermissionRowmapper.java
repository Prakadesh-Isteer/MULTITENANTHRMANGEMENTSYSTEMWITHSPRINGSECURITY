package com.isteer.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.isteer.dto.HttpMethodRoleRights;

public class HttpMethodPermissionRowmapper implements RowMapper<HttpMethodRoleRights> {

	@Override
	public HttpMethodRoleRights mapRow(ResultSet rs, int rowNum) throws SQLException {
		HttpMethodRoleRights permission = new HttpMethodRoleRights();
		permission.setHttpMethod(rs.getString("http_id"));
		permission.setRoleId(rs.getString("role_id"));
		return permission;
	}

}
