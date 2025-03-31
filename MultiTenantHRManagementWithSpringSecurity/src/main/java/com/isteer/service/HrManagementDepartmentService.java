package com.isteer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isteer.entity.Departments;
import com.isteer.entity.Employee;
import com.isteer.repository.DepartmentRepoDaoImpl;

@Service
public class HrManagementDepartmentService {

	@Autowired
	DepartmentRepoDaoImpl repo;

	public int addDepartment(String tenantId, Departments departments) {
		// First, check if the tenant exists
		boolean tenantExists = repo.isTenantExist(departments.getTenantUuid());

		if (!tenantExists) {
			return -1; // Tenant not found
		}

		// Check if the department already exists
		boolean departmentExists = repo.isDepartmentExist(departments.getDepartmentName(), departments.getTenantUuid());

		if (departmentExists) {
			return -2; // Department already exists
		}
		
		
		

		// If tenant and department do not exist, proceed to add the new department
		return repo.addDepartment(tenantId, departments);
	}

	public int updateTenant(Departments department) {
		return repo.updateDepartment(department);

	}

	public List<Departments> getAllDepartments(String tenantUuid) {

		return repo.getAllDepartments(tenantUuid);

	}
	
	public int deleteDepartment(String departmentId) {
		return repo.deleteDepartment(departmentId);
	}
	
	public List<Employee> getAllEmployeesByDepartmentId(String departementId){
		return repo.getAllEmployeesByDepartment(departementId);
		
	}

}
