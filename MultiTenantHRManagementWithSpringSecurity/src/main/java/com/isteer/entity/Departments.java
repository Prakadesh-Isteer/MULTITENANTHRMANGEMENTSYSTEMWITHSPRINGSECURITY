package com.isteer.entity;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotBlank;

@Component
public class Departments {
	private String tenantUuid;
	private String departmentUuid;
	private String departmentHeadId;
	@NotBlank(message = "DEPARTMENT NAME SHOULD NOT BE BLANK")
	private String departmentName;
	@NotBlank(message = "EMAIL FIELD SHOULD NOT BE EMPTY")
	private String email;
	@NotBlank(message = "PHONE NUMBER FIELD SHOULD NOT BE BLANK")
	private String phoneNumber;
	@NotBlank(message = "DESCRIPTION FIELD SHOULD NOT BE BLANK")
	private String description;

	public Departments() {
		// TODO Auto-generated constructor stub
	}

	public String getTenantUuid() {
		return tenantUuid;
	}

	public void setTenantUuid(String tenantUuid) {
		this.tenantUuid = tenantUuid;
	}

	public String getDepartmentUuid() {
		return departmentUuid;
	}

	public void setDepartmentUuid(String departmentUuid) {
		this.departmentUuid = departmentUuid;
	}

	public String getDepartmentHeadId() {
		return departmentHeadId;
	}

	public void setDepartmentHeadId(String departmentHeadId) {
		this.departmentHeadId = departmentHeadId;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Departments(String departmentId, String departmentHeadId, String departmentName, String email,
			String phoneNumber, String description) {
		super();
		this.departmentUuid = departmentId;
		this.departmentHeadId = departmentHeadId;
		this.departmentName = departmentName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.description = description;
	}

}
