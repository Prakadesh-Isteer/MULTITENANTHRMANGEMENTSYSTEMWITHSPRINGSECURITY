package com.isteer.exception;

import com.isteer.enums.HrManagementEnum;

public class DateTooFarInFutureException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	 private final HrManagementEnum error;
		public DateTooFarInFutureException(HrManagementEnum IdException) {
			super(IdException.getStatusMessage());
			   this.error = IdException;
		}
		
		public HrManagementEnum getError() {
			return error;
		}

}
