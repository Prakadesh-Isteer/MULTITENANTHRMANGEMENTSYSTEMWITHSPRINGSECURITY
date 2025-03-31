package com.isteer.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.isteer.entity.Roles;

public class RolesRowmapper implements RowMapper<Roles>{

	@Override
	public Roles mapRow(ResultSet rs, int rowNum) throws SQLException {
		Roles role = new Roles();
		role.setRoleUuid(rs.getString("role_uuid"));
		role.setRoleName(rs.getString("role_name"));
		role.setRoleDescription(rs.getString("description"));
		return role;
	}

}
