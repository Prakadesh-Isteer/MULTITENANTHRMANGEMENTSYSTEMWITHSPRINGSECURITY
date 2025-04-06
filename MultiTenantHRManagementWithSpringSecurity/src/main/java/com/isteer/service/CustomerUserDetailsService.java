package com.isteer.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(CustomerUserDetailsService.class);

	 @Autowired
	 private EmployeeRepoDaoImpl repo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempting to load user details for username: {}", username);

			
        Employee user = repo.findByUserName(username);
        
        
    		
        if(user == null) {
            logger.error("User not found for username: {}", username);

        	throw new EmployeeIdNullException(HrManagementEnum.Employee_id_null);
        }
        logger.debug("User found for username: {}", username);

        List<GrantedAuthority> authorities = new ArrayList<>();
        String role = user.getRoleUuid();
        authorities.add(new SimpleGrantedAuthority("ROLE_" +role));
        logger.debug("Assigned roles to user: {} -> {}", username, authorities);
        logger.info("Successfully loaded user details for username: {}", username);

        return new User(user.getUserName(), user.getPassword(), authorities) ;

	}
			

}
