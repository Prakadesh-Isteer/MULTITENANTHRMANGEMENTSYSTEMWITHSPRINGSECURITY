package com.isteer.exception;

import com.isteer.enums.HrManagementEnum;

public class EmployeeNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final HrManagementEnum error;
	public EmployeeNotFoundException(HrManagementEnum tickectIdException) {
		super(tickectIdException.getStatusMessage());
		   this.error = tickectIdException;
	}
	
	public HrManagementEnum getError() {
		return error;
	}

}
