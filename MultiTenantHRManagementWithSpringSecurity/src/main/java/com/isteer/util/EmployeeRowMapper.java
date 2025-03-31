package com.isteer.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.isteer.entity.Employee;

public class EmployeeRowMapper implements RowMapper<Employee> {

	

	@Override
	public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
		Employee employee = new Employee();
		employee.setEmployeeUuid(rs.getString("employee_uuid"));
		employee.setTenantUuid(rs.getString("tenant_id"));
		employee.setRoleUuid(rs.getString("role_id"));
		employee.setDepartmentUuid(rs.getString("department_id"));
		employee.setUserName(rs.getString("userName"));
		employee.setPassword(rs.getString("password"));
		employee.setFirstName(rs.getString("first_name"));
		employee.setLastName(rs.getString("last_name"));
		employee.setEmail(rs.getString("email"));
		employee.setPhoneNumber(rs.getString("phone"));
		employee.setAddress(rs.getString("address"));
		employee.setDateOfJoining(rs.getDate("date_of_joining").toString());
		employee.setJobTitle(rs.getString("job_title"));
		return employee;
	}

}
