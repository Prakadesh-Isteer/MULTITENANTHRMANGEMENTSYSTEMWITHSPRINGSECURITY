package com.isteer.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.isteer.entity.Departments;

public class DepartmentRowMapper implements RowMapper<Departments> {

	@Override
	public Departments mapRow(ResultSet rs, int rowNum) throws SQLException {
		Departments department = new Departments();
		department.setTenantUuid(rs.getString("tenant_id"));
		department.setDepartmentUuid(rs.getString("department_uuid"));
		department.setDepartmentHeadId(rs.getString("department_head_uuid"));
		department.setDepartmentName(rs.getString("department_name"));
		department.setEmail(rs.getString("contact_email"));
		department.setPhoneNumber(rs.getString("contact_phone"));
		department.setDescription(rs.getString("description"));
		return department;
	}

}
