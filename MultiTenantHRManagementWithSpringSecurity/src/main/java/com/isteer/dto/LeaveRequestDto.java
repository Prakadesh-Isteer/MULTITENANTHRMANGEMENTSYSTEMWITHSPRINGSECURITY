package com.isteer.dto;

import java.time.LocalDate;
import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LeaveRequestDto {
	

	  private String employeeUuid;

	    private LocalDate startDate;
	  @NotNull(message = "END DATE FIELD CANNOT BE EMPTY")
	    private LocalDate endDate;
	  @NotBlank(message = "REASON FIELD CANNOT BE EMPTY")
	    private String reason;
	  
	public String getEmployeeUuid() {
		return employeeUuid;
	}
	public void setEmployeeUuid(String employeeUuid) {
		this.employeeUuid = employeeUuid;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	  

	    
		
}
