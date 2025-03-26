package com.isteer.dto;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LeaveRequestDto {
	

	  private String employeeId;
	  @NotNull(message = "START DATE FIELD CANNOT BE EMPTY")
	    private Date startDate;
	  @NotNull(message = "END DATE FIELD CANNOT BE EMPTY")
	    private Date endDate;
	  @NotBlank(message = "REASON FIELD CANNOT BE EMPTY")
	    private String reason;
	    
	    
		public String getEmployeeId() {
			return employeeId;
		}
		public void setEmployeeId(String employeeId) {
			this.employeeId = employeeId;
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

}
