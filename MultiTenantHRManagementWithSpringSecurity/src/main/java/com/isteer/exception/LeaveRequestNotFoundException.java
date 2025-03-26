package com.isteer.exception;

import com.isteer.enums.HrManagementEnum;

public class LeaveRequestNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	 private final HrManagementEnum error;
	public LeaveRequestNotFoundException(HrManagementEnum IdException) {
	
		super(IdException.getStatusMessage());
		   this.error = IdException;
	}
	
	public HrManagementEnum getError() {
		return error;
	}
}
