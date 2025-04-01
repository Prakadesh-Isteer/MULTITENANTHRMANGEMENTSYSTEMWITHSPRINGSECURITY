package com.isteer.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isteer.dto.UserDetailsDto;
import com.isteer.entity.Employee;
import com.isteer.entity.Roles;
import com.isteer.repository.EmployeeRepoDaoImpl;
import com.isteer.repository.LeaveRepoDaoImpl;

@Service
public class HrManagementEmployeeService {

	
	@Autowired
	EmployeeRepoDaoImpl employeeRepoDaoImpl;

	
	 @Autowired
	    private BCryptPasswordEncoder passwordEncoder;

	    public int registerUser(UserDetailsDto details, String departmentId) {
	        // Encode the password for each user in the list

	            // Encoding the password using BCryptPasswordEncoder
	            details.setPassword(passwordEncoder.encode(details.getPassword()));
	           

	        // Now save the users with the encoded password
	        return employeeRepoDaoImpl.registerEmployee(details, departmentId);
	    }
	
	public List<Employee> getAllUsersByEmployeeUuid(String employeeUuid){
		return employeeRepoDaoImpl.getUsersById(employeeUuid);
	}
	    
	    @Transactional

	    public List<Employee> getAllUsers() {
	        // Get the logged-in user's username
	        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
	        Employee loggedInEmployee = employeeRepoDaoImpl.findByUserName(userName);

	        // Fetch all available roles to check if the logged-in user is an Admin, HR, or Department Head
	        List<Roles> availableRoles = employeeRepoDaoImpl.getAllAvailableRoles();

	        // Check if the logged-in user is an Admin (SUPER_ADMIN role)
	        boolean isSuperAdmin = availableRoles.stream()
	                .anyMatch(role -> role.getRoleName().equals("SUPER_ADMIN") && role.getRoleUuid().equals(loggedInEmployee.getRoleUuid()));

	        // Check if the logged-in user is HR (HR role)
	        boolean isHR = availableRoles.stream()
	                .anyMatch(role -> role.getRoleName().equals("HR_MANAGER") && role.getRoleUuid().equals(loggedInEmployee.getRoleUuid()));

	        // Check if the logged-in user is a Department Head (HOD role)
	        boolean isDepartmentHead = availableRoles.stream()
	                .anyMatch(role -> role.getRoleName().equals("HOD") && role.getRoleUuid().equals(loggedInEmployee.getRoleUuid()));

	        // If the user is a Super Admin, fetch all employees from all tenants and departments
	        if (isSuperAdmin) {
	            return employeeRepoDaoImpl.getAllUsers();  // Fetch all employees
	        }

	        // If the user is HR, fetch only employees from the same tenant
	        if (isHR) {
	            String tenantUuid = loggedInEmployee.getTenantUuid();  // Fetch the tenantUuid from the logged-in employee
	            return employeeRepoDaoImpl.getEmployeesByTenant(tenantUuid);  // Fetch employees for the specific tenant
	        }

	        // If the user is a Department Head, fetch employees from the same department
	        if (isDepartmentHead) {
	            String departmentUuid = loggedInEmployee.getDepartmentUuid();  // Fetch the departmentUuid from the logged-in employee
	            return employeeRepoDaoImpl.getEmployeesByDepartment(departmentUuid);  // Fetch employees for the specific department
	        }

	        // If none of the above conditions are met, deny access or return an empty list
	        return new ArrayList<>();  // No access to users
	    }

	
	
	public int addRole(Roles role) {
		return employeeRepoDaoImpl.addRole(role);
	}
	
	public int updateUser(UserDetailsDto details) {
		
	            // Encoding the password using BCryptPasswordEncoder
	    details.setPassword(passwordEncoder.encode(details.getPassword()));
	       
		return employeeRepoDaoImpl.updateUser(details);
	}
	
	public int deleteEmployee(String employeeId) {
		return employeeRepoDaoImpl.deleteEmployee(employeeId);
	}
	
	public List<Roles> getAllAvailableRoles() {
		return employeeRepoDaoImpl.getAllAvailableRoles();
		
	}
	
	public int updateUserRole(String employeeId ,String roleId) {
		return employeeRepoDaoImpl.updateUserRole(employeeId,roleId);
	}
		
	
}
