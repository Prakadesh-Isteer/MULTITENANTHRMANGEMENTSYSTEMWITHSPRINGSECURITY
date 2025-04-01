package com.isteer.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.isteer.dto.UserDetailsDto;
import com.isteer.entity.Employee;

public class UserPrincipal implements UserDetails {
	
	@Autowired
	Employee user;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserPrincipal(Employee user2) {
		this.user = user2;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorites = new ArrayList<>();
		authorites.add(new SimpleGrantedAuthority(user.getRoleUuid()));
		return authorites;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		System.out.println(user.getUserName());
		return user.getUserName();
	}
	
	public String getRole() {
		return user.getRoleUuid();
	}

   
}
