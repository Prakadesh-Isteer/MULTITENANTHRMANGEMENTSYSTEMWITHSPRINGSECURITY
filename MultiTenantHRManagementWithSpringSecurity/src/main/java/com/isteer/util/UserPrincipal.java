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

public class UserPrincipal implements UserDetails {
	
	@Autowired
	UserDetailsDto user;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserPrincipal(UserDetailsDto user2) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorites = new ArrayList<>();
		authorites.add(new SimpleGrantedAuthority(user.getRoleId()));
		System.out.println(user.getUserName());
		System.out.println(user.getPassword());
		System.out.println(user.getRoleId());
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
		return user.getRoleId();
	}

   
}
