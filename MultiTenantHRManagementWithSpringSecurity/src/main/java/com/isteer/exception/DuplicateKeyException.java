package com.isteer.exception;

import com.isteer.enums.HrManagementEnum;

public class DuplicateKeyException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final HrManagementEnum error;
	public DuplicateKeyException(HrManagementEnum Exception) {
		super(Exception.getStatusMessage());
		   this.error = Exception;
	}
	
	public HrManagementEnum getError() {
		return error;
	}

}
