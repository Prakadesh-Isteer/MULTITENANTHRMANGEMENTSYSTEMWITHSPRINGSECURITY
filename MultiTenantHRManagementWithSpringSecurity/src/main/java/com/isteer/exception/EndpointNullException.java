package com.isteer.exception;

import com.isteer.enums.HrManagementEnum;

public class EndpointNullException extends RuntimeException {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	 private final HrManagementEnum error;
	public EndpointNullException(HrManagementEnum IdException) {
	
		super(IdException.getStatusMessage());
		   this.error = IdException;
	}
	
	public HrManagementEnum getError() {
		return error;
	}
	
}
