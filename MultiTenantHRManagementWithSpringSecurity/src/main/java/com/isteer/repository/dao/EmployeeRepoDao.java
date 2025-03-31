package com.isteer.repository.dao;

import java.util.List;

import com.isteer.dto.UserDetailsDto;
import com.isteer.entity.Employee;
import com.isteer.entity.Roles;

public interface EmployeeRepoDao {

	public int registerEmployee(UserDetailsDto detailsList , String departmentId);
	
	public int addRole(Roles role);
	
	public List<Employee> getAllUsers();
	
	public List<Employee> getUsersById(String userId);
	
	public int updateUser(UserDetailsDto details);

	public int deleteEmployee(String employeeId);
	
	public List<Roles> getAllAvailableRoles();
	

	public int updateUserRole(String employeeId, String roleId);
	
}
