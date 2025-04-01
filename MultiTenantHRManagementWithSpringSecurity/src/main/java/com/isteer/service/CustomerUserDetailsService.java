package com.isteer.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.isteer.entity.Employee;
import com.isteer.enums.HrManagementEnum;
import com.isteer.exception.EmployeeIdNullException;
import com.isteer.repository.EmployeeRepoDaoImpl;

@Component
public class CustomerUserDetailsService implements UserDetailsService {

	 
	 @Autowired
	 private EmployeeRepoDaoImpl repo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	
		 // Retrieve user from the repository based on userName
			System.out.println("22222222222222222222222222222");
        Employee user = repo.findByUserName(username);
        
        
    		
        if(user == null) {
        	throw new EmployeeIdNullException(HrManagementEnum.Employee_id_null);
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        String role = user.getRoleUuid();
        authorities.add(new SimpleGrantedAuthority("ROLE_" +role));
		
        return new User(user.getUserName(), user.getPassword(), authorities) ;

	}
			

}
