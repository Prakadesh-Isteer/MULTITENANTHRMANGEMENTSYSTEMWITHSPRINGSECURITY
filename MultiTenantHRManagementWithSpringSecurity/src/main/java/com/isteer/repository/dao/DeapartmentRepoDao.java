package com.isteer.repository.dao;

import java.util.List;

import com.isteer.entity.Departments;
import com.isteer.entity.Employee;

public interface DeapartmentRepoDao {
	
	public int addDepartment(String tenantId,Departments department);
	public boolean isTenantExist(String tenantId);
	public boolean isDepartmentExist(String departmentName, String tenantId);
	public int updateDepartment(Departments department);
	public List<Departments> getAllDepartments(String tenantId);
	public int deleteDepartment(String departmentId) ;
	public List<Employee> getAllEmployeesByDepartment(String departmentId);
}
