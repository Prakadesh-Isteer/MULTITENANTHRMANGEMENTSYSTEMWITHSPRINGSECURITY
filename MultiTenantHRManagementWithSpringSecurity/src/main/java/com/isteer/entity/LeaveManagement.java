package com.isteer.entity;

import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class LeaveManagement {
	
	private String leaveUuid;
    private String employeeUuid;
    private String departmentUuid;
    private Date startDate;
    private Date endDate;
    private String reason;
    private String status;
    private Date appliedAt;
    private String approvedBy;
    private Date approvedAt;

	
   
	public String getLeaveUuid() {
		return leaveUuid;
	}
	public void setLeaveUuid(String leaveUuid) {
		this.leaveUuid = leaveUuid;
	}
	public String getEmployeeUuid() {
		return employeeUuid;
	}
	public void setEmployeeUuid(String employeeUuid) {
		this.employeeUuid = employeeUuid;
	}
	public String getDepartmentUuid() {
		return departmentUuid;
	}
	public void setDepartmentUuid(String departmentUuid) {
		this.departmentUuid = departmentUuid;
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
	public Date getAppliedAt() {
		return appliedAt;
	}
	public void setAppliedAt(Date appliedAt) {
		this.appliedAt = appliedAt;
	}
	public String getApprovedBy() {
		return approvedBy;
	}
	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}
	public Date getApprovedAt() {
		return approvedAt;
	}
	public void setApprovedAt(Date approvedAt) {
		this.approvedAt = approvedAt;
	}
	

}