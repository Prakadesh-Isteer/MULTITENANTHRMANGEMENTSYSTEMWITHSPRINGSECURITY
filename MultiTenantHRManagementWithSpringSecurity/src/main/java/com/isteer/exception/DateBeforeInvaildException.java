package com.isteer.exception;

import com.isteer.enums.HrManagementEnum;

public class DateBeforeInvaildException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	 private final HrManagementEnum error;
		public DateBeforeInvaildException(HrManagementEnum IdException) {
			super(IdException.getStatusMessage());
			   this.error = IdException;
		}
		
		public HrManagementEnum getError() {
			return error;
		}


}
