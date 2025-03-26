package com.isteer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.isteer.dto.UserDetailsDto;
import com.isteer.entity.Employee;
import com.isteer.entity.Roles;
import com.isteer.repository.EmployeeRepoDaoImpl;

@Service
public class HrManagementEmployeeService {

	
	@Autowired
	EmployeeRepoDaoImpl repo;
	
	 @Autowired
	    private BCryptPasswordEncoder passwordEncoder;

	    public int registerUser(List<UserDetailsDto> details, String departmentId) {
	        // Encode the password for each user in the list
	        for (UserDetailsDto userDetails : details) {
	            // Encoding the password using BCryptPasswordEncoder
	            userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
	        }

	        // Now save the users with the encoded password
	        return repo.registerEmployees(details, departmentId);
	    }
	
	public List<Employee> getAllUsers(){
		return repo.getAllUsers();
	}
	
	public List<Employee> getuserById(String employeeId){
		return repo.getUsersById(employeeId);
	}
	
	public int addRole(Roles role) {
		return repo.addRole(role);
	}
	
	public int updateUser(UserDetailsDto details) {
		return repo.updateUser(details);
	}
	
	public int deleteEmployee(String employeeId) {
		return repo.deleteEmployee(employeeId);
	}
	
	public List<Roles> getAllAvailableRoles() {
		return repo.getAllAvailableRoles();
		
	}
	
	public int updateUserRole(String employeeId ,String roleId) {
		return repo.updateUserRole(employeeId,roleId);
	}
		
	
}
