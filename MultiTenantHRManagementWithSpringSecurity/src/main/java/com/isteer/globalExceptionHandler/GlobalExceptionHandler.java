package com.isteer.globalExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.isteer.dto.ErrorMessageDto;
import com.isteer.enums.HrManagementEnum;
import com.isteer.exception.InsertionFailedException;
import com.isteer.exception.LeaveRequestNotFoundException;
import com.isteer.exception.TenantIdNullException;
import com.isteer.exception.EmployeeNotFoundException;

import com.isteer.exception.DepartmentNotFoundException;

@ControllerAdvice
//i used this annotation to display the data in json form directly. 
@ResponseBody
public class GlobalExceptionHandler {
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessageDto  Exception(Exception e) {
		ErrorMessageDto invaildOperation = new ErrorMessageDto();
		invaildOperation.setErrorCode(9888);
		invaildOperation.setErrorMessage(e.getMessage());
		e.printStackTrace();
		return invaildOperation;
	}
	@ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessageDto MethodArgumentNotValidException(org.springframework.web.bind.MethodArgumentNotValidException e) {
     ErrorMessageDto invaildOperation = new ErrorMessageDto();
		invaildOperation.setErrorCode(9321);
		invaildOperation.setErrorMessage(e.getBindingResult().getFieldError().getDefaultMessage());
		return invaildOperation;
	}
	
	@ExceptionHandler(TenantIdNullException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessageDto TenantIdNullException(TenantIdNullException e) {
     ErrorMessageDto invaildOperation = new ErrorMessageDto();
		invaildOperation.setErrorCode(HrManagementEnum.Tenant_id_null.getStatusCode());
		invaildOperation.setErrorMessage(HrManagementEnum.Tenant_id_null.getStatusMessage());
		return invaildOperation;
	}
	
	@ExceptionHandler(com.isteer.exception.DepartmentIdNullException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessageDto DepartmentIdNullException(com.isteer.exception.DepartmentIdNullException e) {
     ErrorMessageDto invaildOperation = new ErrorMessageDto();
		invaildOperation.setErrorCode(HrManagementEnum.Department_id_null.getStatusCode());
		invaildOperation.setErrorMessage(HrManagementEnum.Department_id_null.getStatusMessage());
		return invaildOperation;
	}
	@ExceptionHandler(com.isteer.exception.EmployeeIdNullException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessageDto EmployeeIdNullException(com.isteer.exception.EmployeeIdNullException e) {
     ErrorMessageDto invaildOperation = new ErrorMessageDto();
		invaildOperation.setErrorCode(HrManagementEnum.Employee_id_null.getStatusCode());
		invaildOperation.setErrorMessage(HrManagementEnum.Employee_id_null.getStatusMessage());
		return invaildOperation;
	}
	@ExceptionHandler(com.isteer.exception.RoleIdNullException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessageDto RoleIdNullException(com.isteer.exception.RoleIdNullException e) {
     ErrorMessageDto invaildOperation = new ErrorMessageDto();
		invaildOperation.setErrorCode(HrManagementEnum.Role_id_null.getStatusCode());
		invaildOperation.setErrorMessage(HrManagementEnum.Role_id_null.getStatusMessage());
		return invaildOperation;
	}
	
	
	@ExceptionHandler(org.springframework.dao.DuplicateKeyException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessageDto DuplicateKeyException(org.springframework.dao.DuplicateKeyException e) {
     ErrorMessageDto invaildOperation = new ErrorMessageDto();
		invaildOperation.setErrorCode(HrManagementEnum.DUPLICATE_KEY_EXCEPTION.getStatusCode());
		invaildOperation.setErrorMessage(HrManagementEnum.DUPLICATE_KEY_EXCEPTION.getStatusMessage());
		return invaildOperation;
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessageDto IllegalArgumentException(IllegalArgumentException e) {
     ErrorMessageDto invaildOperation = new ErrorMessageDto();
		invaildOperation.setErrorCode(HrManagementEnum.ILLEGAL_AGRUMENT.getStatusCode());
		invaildOperation.setErrorMessage(HrManagementEnum.ILLEGAL_AGRUMENT.getStatusMessage());
		return invaildOperation;
	}
	
	@ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessageDto MissingServletRequestParameterException(org.springframework.web.bind.MissingServletRequestParameterException e) {
     ErrorMessageDto invaildOperation = new ErrorMessageDto();
		invaildOperation.setErrorCode(HrManagementEnum.MissserveletException.getStatusCode());
		invaildOperation.setErrorMessage(HrManagementEnum.MissserveletException.getStatusMessage());
		return invaildOperation;
	}
	
	@ExceptionHandler(com.isteer.exception.IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessageDto IllegalArgumentException1( com.isteer.exception.IllegalArgumentException e) {
     ErrorMessageDto invaildOperation = new ErrorMessageDto();
		invaildOperation.setErrorCode(HrManagementEnum.Illegal_Argumnet_role.getStatusCode());
		invaildOperation.setErrorMessage(HrManagementEnum.Illegal_Argumnet_role.getStatusMessage());
		return invaildOperation;
	}
	
	@ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessageDto DataIntegrityViolationException(org.springframework.dao.DataIntegrityViolationException e) {
     ErrorMessageDto invaildOperation = new ErrorMessageDto();
		invaildOperation.setErrorCode(HrManagementEnum.Date_mismatch.getStatusCode());
		invaildOperation.setErrorMessage(HrManagementEnum.Date_mismatch.getStatusMessage());
		return invaildOperation;
	}
	
	@ExceptionHandler(InsertionFailedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessageDto InsertionFailedException(InsertionFailedException e) {
     ErrorMessageDto invaildOperation = new ErrorMessageDto();
		invaildOperation.setErrorCode(HrManagementEnum.Insertion_failed_Exception.getStatusCode());
		invaildOperation.setErrorMessage(HrManagementEnum.Insertion_failed_Exception.getStatusMessage());
		return invaildOperation;
	}
	
	@ExceptionHandler(EmployeeNotFoundException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessageDto EmployeeNotFoundException(EmployeeNotFoundException e) {
     ErrorMessageDto invaildOperation = new ErrorMessageDto();
		invaildOperation.setErrorCode(HrManagementEnum.EMPLOYEE_VALID_NOT_FOUND.getStatusCode());
		invaildOperation.setErrorMessage(HrManagementEnum.EMPLOYEE_VALID_NOT_FOUND.getStatusMessage());
		return invaildOperation;
	}
	
	@ExceptionHandler(DepartmentNotFoundException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessageDto DepartmentNotFoundException(DepartmentNotFoundException e) {
     ErrorMessageDto invaildOperation = new ErrorMessageDto();
		invaildOperation.setErrorCode(HrManagementEnum.DEPARTMENT_VALID_NOT_FOUND.getStatusCode());
		invaildOperation.setErrorMessage(HrManagementEnum.DEPARTMENT_VALID_NOT_FOUND.getStatusMessage());
		return invaildOperation;
	}
	
	@ExceptionHandler(LeaveRequestNotFoundException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessageDto LeaveRequestNotFoundException(LeaveRequestNotFoundException e) {
     ErrorMessageDto invaildOperation = new ErrorMessageDto();
		invaildOperation.setErrorCode(HrManagementEnum.LEAVE_REQUEST_NOT_FOUND.getStatusCode());
		invaildOperation.setErrorMessage(HrManagementEnum.LEAVE_REQUEST_NOT_FOUND.getStatusMessage());
		return invaildOperation;
	}
	
	@ExceptionHandler(io.jsonwebtoken.security.SignatureException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessageDto SignatureException(io.jsonwebtoken.security.SignatureException e) {
     ErrorMessageDto invaildOperation = new ErrorMessageDto();
		invaildOperation.setErrorCode(HrManagementEnum.LEAVE_REQUEST_NOT_FOUND.getStatusCode());
		invaildOperation.setErrorMessage(HrManagementEnum.LEAVE_REQUEST_NOT_FOUND.getStatusMessage());
		return invaildOperation;
	}
	
	@ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessageDto responseStatusException(org.springframework.web.server.ResponseStatusException e) {
     ErrorMessageDto invaildOperation = new ErrorMessageDto();
		invaildOperation.setErrorCode(HrManagementEnum.Illegal_Argumnet_role.getStatusCode());
		invaildOperation.setErrorMessage(HrManagementEnum.Illegal_Argumnet_role.getStatusMessage());
		return invaildOperation;
	}
	
	@ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorMessageDto AuthorizationDeniedException (org.springframework.security.authorization.AuthorizationDeniedException e) {
		ErrorMessageDto invaildOperation = new ErrorMessageDto();
		invaildOperation.setErrorCode(HrManagementEnum.AUTHORIZATION_DEINED.getStatusCode());
		invaildOperation.setErrorMessage(HrManagementEnum.AUTHORIZATION_DEINED.getStatusMessage());
		return invaildOperation;
	}
	
	
	@ExceptionHandler(org.springframework.dao.EmptyResultDataAccessException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessageDto EmptyResultDataAccessException (org.springframework.dao.EmptyResultDataAccessException e) {
		ErrorMessageDto invaildOperation = new ErrorMessageDto();
		invaildOperation.setErrorCode(HrManagementEnum.EMPTY_DATA_EXCEPTION.getStatusCode());
		invaildOperation.setErrorMessage(HrManagementEnum.EMPTY_DATA_EXCEPTION.getStatusMessage());
		return invaildOperation;
	}
	
	@ExceptionHandler(com.isteer.exception.BadCredentialsException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessageDto BadCredentialsException (com.isteer.exception.BadCredentialsException e) {
		ErrorMessageDto invaildOperation = new ErrorMessageDto();
		invaildOperation.setErrorCode(HrManagementEnum.Bad_credentials_exception.getStatusCode());
		invaildOperation.setErrorMessage(HrManagementEnum.Bad_credentials_exception.getStatusMessage());
		return invaildOperation;
	}
	
	
}
