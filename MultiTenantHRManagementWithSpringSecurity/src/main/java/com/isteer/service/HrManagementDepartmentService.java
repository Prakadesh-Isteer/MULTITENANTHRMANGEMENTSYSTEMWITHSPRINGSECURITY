package com.isteer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isteer.entity.Departments;
import com.isteer.entity.Employee;
import com.isteer.entity.Roles;
import com.isteer.repository.DepartmentRepoDaoImpl;
import com.isteer.repository.EmployeeRepoDaoImpl;

@Service
public class HrManagementDepartmentService {

	@Autowired
	DepartmentRepoDaoImpl departmentRepoDaoImpl;
	
	@Autowired
	EmployeeRepoDaoImpl employeeRepoDaoImpl;

	public int addDepartment(String tenantId, Departments departments) {
		// First, check if the tenant exists
		boolean tenantExists = departmentRepoDaoImpl.isTenantExist(departments.getTenantUuid());

		if (!tenantExists) {
			return -1; // Tenant not found
		}

		// Check if the department already exists
		boolean departmentExists = departmentRepoDaoImpl.isDepartmentExist(departments.getDepartmentName(), departments.getTenantUuid());

		if (departmentExists) {
			return -2; // Department already exists
		}
				

		// If tenant and department do not exist, proceed to add the new department
		return departmentRepoDaoImpl.addDepartment(tenantId, departments);
	}

	public int updateTenant(Departments department) {
		return departmentRepoDaoImpl.updateDepartment(department);

	}
	
	
	@Transactional
	public List<Departments> getAllDepartments(String tenantUuid) {
	    // Extract the tenant ID from the currently authenticated user's JWT token
	    String userName = SecurityContextHolder.getContext().getAuthentication().getName();
	    Employee loggedInEmployee = employeeRepoDaoImpl.findByUserName(userName);

	    // Fetch all available roles from the roles table
	    List<Roles> getAllAvailableRoles = employeeRepoDaoImpl.getAllAvailableRoles();

	    // Check if the logged-in user has the SUPER_ADMIN role
	    boolean isSuperAdmin = getAllAvailableRoles.stream()
	            .anyMatch(role -> role.getRoleName().equals("SUPER_ADMIN") && role.getRoleUuid().equals(loggedInEmployee.getRoleUuid()));

	    // If the user is a Super Admin, fetch all departments from all tenants
	    if (isSuperAdmin) {
	        return departmentRepoDaoImpl.getAllDepartments();  // Fetch all departments
	    }

	    // If the user is not a Super Admin, fetch departments only for their tenant
	    String wrkTenantUuid = loggedInEmployee.getTenantUuid();  // Fetch the tenantUuid from the logged-in employee
	    return departmentRepoDaoImpl.getAllDepartmentsByTenants(wrkTenantUuid);  // Fetch departments for the specific tenant
	}

	
	public int deleteDepartment(String departmentId) {
		return departmentRepoDaoImpl.deleteDepartment(departmentId);
	}
	
	public List<Employee> getAllEmployeesByDepartmentId(String departementId){
		return departmentRepoDaoImpl.getAllEmployeesByDepartment(departementId);
		
	}

}
