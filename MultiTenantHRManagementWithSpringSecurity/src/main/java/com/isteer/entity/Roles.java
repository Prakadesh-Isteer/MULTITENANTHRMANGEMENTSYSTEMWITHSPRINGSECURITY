package com.isteer.entity;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotBlank;

@Component
public class Roles {
    
	private String roleId;
	@NotBlank(message = "ROLE NAME FIELD CANNOT BE EMPTY")
	private String roleName;
	@NotBlank(message = "ROLE DESCRIPTION IELD CANNOT BE EMPTY")
	private String roleDescription;
	
	public Roles() {
		// TODO Auto-generated constructor stub
	}
	
	public Roles(String roleId, String roleName, String roleDescription) {
		super();
		this.roleId = roleId;
		this.roleName = roleName;
		this.roleDescription = roleDescription;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getRoleDescription() {
		return roleDescription;
	}
	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}
}
