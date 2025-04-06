package com.isteer.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isteer.entity.Departments;
import com.isteer.entity.Employee;
import com.isteer.entity.Roles;
import com.isteer.enums.HrManagementEnum;
import com.isteer.exception.TenantIdNullException;
import com.isteer.repository.DepartmentRepoDaoImpl;
import com.isteer.repository.EmployeeRepoDaoImpl;

@Service
public class HrManagementDepartmentService {

    private static final Logger logger = LogManager.getLogger(HrManagementDepartmentService.class);

	
	@Autowired
	DepartmentRepoDaoImpl departmentRepoDaoImpl;
	
	@Autowired
	EmployeeRepoDaoImpl employeeRepoDaoImpl;

	public int addDepartment(String tenantId, Departments departments) {
        logger.info("Attempting to add department: {} for tenant: {}", departments.getDepartmentName(), tenantId);

		  if (departments.getTenantUuid().trim().isBlank()) {
	            logger.error("Tenant ID is null or empty");

	  	        throw new TenantIdNullException(HrManagementEnum.Tenant_id_null);
	  	    }
		// First, check if the tenant exists
		boolean tenantExists = departmentRepoDaoImpl.isTenantExist(departments.getTenantUuid());

		if (!tenantExists) {
            logger.error("Tenant with ID: {} not found", departments.getTenantUuid());

			return -1; // Tenant not found
		}

		// Check if the department already exists
		boolean departmentExists = departmentRepoDaoImpl.isDepartmentExist(departments.getDepartmentName(), departments.getTenantUuid());

		if (departmentExists) {
            logger.error("Department {} already exists for tenant {}", departments.getDepartmentName(), departments.getTenantUuid());

			return -2; // Department already exists
		}
				
        logger.info("Successfully added department: {}", departments.getDepartmentName());

		// If tenant and department do not exist, proceed to add the new department
		return departmentRepoDaoImpl.addDepartment(tenantId, departments);
	}

	public int updateTenant(Departments department) {
        logger.info("Attempting to update department: {}", department.getDepartmentName());

		return departmentRepoDaoImpl.updateDepartment(department);

	}
	
	
	@Transactional
	public List<Departments> getAllDepartments(String tenantUuid) {
  
	    String userName = SecurityContextHolder.getContext().getAuthentication().getName();
	     logger.info("Fetching all departments for user: {}", userName);
	    Employee loggedInEmployee = employeeRepoDaoImpl.findByUserName(userName);


	    // Fetch all available roles from the roles table
	    List<Roles> getAllAvailableRoles = employeeRepoDaoImpl.getAllAvailableRoles();

	    // Check if the logged-in user has the SUPER_ADMIN role
	    boolean isSuperAdmin = getAllAvailableRoles.stream()
	            .anyMatch(role -> role.getRoleName().equals("SUPER_ADMIN") && role.getRoleUuid().equals(loggedInEmployee.getRoleUuid()));

	    // If the user is a Super Admin, fetch all departments from all tenants
	    if (isSuperAdmin) {
            logger.info("User is a Super Admin. Fetching all departments.");

	        return departmentRepoDaoImpl.getAllDepartments();  // Fetch all departments
	    }

	    // If the user is not a Super Admin, fetch departments only for their tenant
	    String wrkTenantUuid = loggedInEmployee.getTenantUuid();  // Fetch the tenantUuid from the logged-in employee
        logger.info("User is not a Super Admin. Fetching departments for tenant: {}", wrkTenantUuid);

	    return departmentRepoDaoImpl.getAllDepartmentsByTenants(wrkTenantUuid);  // Fetch departments for the specific tenant
	}

	
	public int deleteDepartment(String departmentId) {
        logger.info("Attempting to delete department with ID: {}", departmentId);

		return departmentRepoDaoImpl.deleteDepartment(departmentId);
	}
	
	public List<Employee> getAllEmployeesByDepartmentId(String departementId){
        logger.info("Fetching all employees for department ID: {}", departementId);

		return departmentRepoDaoImpl.getAllEmployeesByDepartment(departementId);
		
	}

}
