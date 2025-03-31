package com.isteer.entity;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotBlank;

@Component
public class Roles {
    
	private String roleUuid;
	@NotBlank(message = "ROLE NAME FIELD CANNOT BE EMPTY")
	private String roleName;
	@NotBlank(message = "ROLE DESCRIPTION IELD CANNOT BE EMPTY")
	private String roleDescription;
	
	public Roles() {
		// TODO Auto-generated constructor stub
	}
	
	public String getRoleUuid() {
		return roleUuid;
	}

	public void setRoleUuid(String roleUuid) {
		this.roleUuid = roleUuid;
	}
	
	public Roles(String roleId, String roleName, String roleDescription) {
		super();
		this.roleUuid = roleId;
		this.roleName = roleName;
		this.roleDescription = roleDescription;
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
