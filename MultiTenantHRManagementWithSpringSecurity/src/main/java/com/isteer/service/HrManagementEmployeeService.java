package com.isteer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
	
	public List<Employee> getAllUsers(){
		return employeeRepoDaoImpl.getAllUsers();
	}
	
	public Employee getuserById(String userId){
		
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		Employee loggedInEmployee = employeeRepoDaoImpl.findByUserName(userName);

		return loggedInEmployee;
	
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
