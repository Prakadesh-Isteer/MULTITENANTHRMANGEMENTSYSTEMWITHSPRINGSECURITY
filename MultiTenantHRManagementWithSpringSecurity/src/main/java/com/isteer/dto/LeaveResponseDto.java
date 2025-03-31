package com.isteer.dto;

import java.util.Date;

public class LeaveResponseDto {
	
	    private String leaveUuid;
		private String employeeUuid;
	    private String departmentName;
	    private Date startDate;
	    private Date endDate;
	    private String reason;
	    private String status;
	 
	    
	    public String getLeaveUuid() {
		return leaveUuid;
	}
	public void setLeaveUuid(String leaveUuid) {
		this.leaveUuid = leaveUuid;
	}
	
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getEmployeeUuid() {
		return employeeUuid;
	}
	public void setEmployeeUuid(String employeeUuid) {
		this.employeeUuid = employeeUuid;
	}


}
