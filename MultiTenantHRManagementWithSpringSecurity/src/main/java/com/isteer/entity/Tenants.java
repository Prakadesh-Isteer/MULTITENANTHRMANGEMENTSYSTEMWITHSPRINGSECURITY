package com.isteer.entity;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotBlank;

@Component
public class Tenants {

	private String tenantUuid;
	@NotBlank(message = "TENANT NAME FIELD SHOULD NOT BE EMPTY")
	private String tenantName;
	@NotBlank(message = "TENANT ADDRESS FIELD SHOULD NOT BE EMPTY")
	private String address;
	@NotBlank(message = "TENANT EMAIL FIELD SHOULD NOT BE EMPTY")
	private String email;
	@NotBlank(message = "TENANT PHONENUMBER FIELD SHOULD NOT BE EMPTY")
	private String phoneNumber;
	@NotBlank(message = "TENANT COUNTRY FIELD SHOULD NOT BE EMPTY")
	private String country;
	@NotBlank(message = "TENANT STATE FIELD SHOULD NOT BE EMPTY")
	private String state;
	@NotBlank(message = "TENANT CITY FIELD SHOULD NOT BE EMPTY")
	private String city;
	
	
	public String getTenantName() {
		return tenantName;
	}
	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
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
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getTenantUuid() {
		return tenantUuid;
	}
	public void setTenantUuid(String tenantUuid) {
		this.tenantUuid = tenantUuid;
	}
	


}
