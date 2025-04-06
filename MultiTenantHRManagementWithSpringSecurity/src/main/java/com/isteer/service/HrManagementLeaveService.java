package com.isteer.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isteer.dto.LeaveRequestDto;
import com.isteer.entity.Employee;
import com.isteer.entity.LeaveManagement;
import com.isteer.enums.HrManagementEnum;
import com.isteer.exception.DateBeforeInvaildException;
import com.isteer.exception.DateTooFarInFutureException;
import com.isteer.exception.EmployeeNotFoundException;
import com.isteer.exception.InternalServerError;
import com.isteer.exception.LeaveRequestNotFoundException;
import com.isteer.exception.LeaveRequestNullException;
import com.isteer.repository.EmployeeRepoDaoImpl;
import com.isteer.repository.LeaveRepoDaoImpl;

@Service
public class HrManagementLeaveService {

	@Autowired
	EmployeeRepoDaoImpl employeeRepoDaoImpl;
	
	@Autowired
	LeaveRepoDaoImpl leaveRepoDaoImpl;

    private static final Logger logger = LogManager.getLogger(HrManagementLeaveService.class);

	
	@Transactional

	public int applyLeave(LeaveRequestDto leaveRequestDto) {
		 try {  
			 // Get the logged-in user details
	    String userName = SecurityContextHolder.getContext().getAuthentication().getName();
	    Employee loggedInEmployee = employeeRepoDaoImpl.findByUserName(userName);
	    String departmentId = loggedInEmployee.getDepartmentUuid();
	    String userId = loggedInEmployee.getEmployeeUuid();
	    // Set the employee UUID in the leave request DTO
	    leaveRequestDto.setEmployeeUuid(userId);

	    // Set the start date to the current date
	    LocalDate startDate = LocalDate.now();

	    // Ensure the end date is not before the start date
	    if (leaveRequestDto.getEndDate().isBefore(startDate)) {
	    logger.warn("Leave end date is in the past for employee: {}", leaveRequestDto.getEmployeeUuid());

         throw new DateBeforeInvaildException(HrManagementEnum.Date_Exception);
        
	    }
	    
	 // Check if the end date is more than 3 days in the future
	    if (leaveRequestDto.getEndDate().isAfter(LocalDate.now().plusDays(3))) {
            logger.warn("Leave end date is excced for employee: {}", leaveRequestDto.getEmployeeUuid());

	        throw new DateTooFarInFutureException(HrManagementEnum.Date_Future_exception); // Date cannot be more than 3 days in the future
	    }

	    // Now we call the repository to apply the leave
        logger.info("Applying leave for employee: {}", leaveRequestDto.getEmployeeUuid());

	        return leaveRepoDaoImpl.applyLeave(leaveRequestDto, departmentId, startDate);
	    } catch (EmployeeNotFoundException ex) {
	        throw new EmployeeNotFoundException(HrManagementEnum.EMPLOYEE_VALID_NOT_FOUND);
	        
	    }
	}


	
	@Transactional
	public List<LeaveManagement> getAllLeavesByStatus(String status) {
	    // Get the logged-in employee (department head)
		try {
	    String userName = SecurityContextHolder.getContext().getAuthentication().getName();
	    Employee loggedInEmployee = employeeRepoDaoImpl.findByUserName(userName);
	    
	    // Get the department ID of the department head
	    String departmentId = loggedInEmployee.getDepartmentUuid();
        logger.info("Fetching all leave requests with status: {} for department: {}", status, departmentId);
 
	    // Pass the department ID and status to the repository layer to query leaves
	    return leaveRepoDaoImpl.getAllLeavesByStatus(departmentId, status);
	} catch (Exception e) {
        logger.error("Error fetching leave requests by status", e);

        throw new InternalServerError(HrManagementEnum.INTERNAL_SERVER_ERROR);
    }

	}

	
	@Transactional
	public boolean approveLeaveRequest(String leaveUuid) {
		 if (leaveUuid.trim().isBlank()) {
	  	        throw new LeaveRequestNullException(HrManagementEnum.Leave_id_null);
	  	    }
		try {
			
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		Employee loggedInEmployee = employeeRepoDaoImpl.findByUserName(userName);
		String departmentId = loggedInEmployee.getDepartmentUuid();
		String userId = loggedInEmployee.getEmployeeUuid();
        logger.info("Approving leave request: {} for employee: {}", leaveUuid, userId);

	    return leaveRepoDaoImpl.approveLeaveRequest(leaveUuid, userId, departmentId);
		}catch(Exception e) {
            logger.error("Error approving leave request: {}", leaveUuid);

			  throw new LeaveRequestNotFoundException(HrManagementEnum.LEAVE_REQUEST_NOT_FOUND);
		}

	}

	 
	 @Transactional
	    public boolean rejectLeaveRequest(String leaveUuid) {
		 if (leaveUuid.trim().isBlank()) {
	  	        throw new LeaveRequestNullException(HrManagementEnum.Leave_id_null);
	  	    }
		 try {
	            logger.info("Rejecting leave request: {} for employee: {}", leaveUuid);

		 String userName = SecurityContextHolder.getContext().getAuthentication().getName();
			Employee loggedInEmployee = employeeRepoDaoImpl.findByUserName(userName);
			String departmentId = loggedInEmployee.getDepartmentUuid();
			String userId = loggedInEmployee.getEmployeeUuid();
		 return leaveRepoDaoImpl.rejectLeaveRequest(leaveUuid, userId, departmentId);
	 }catch(Exception e) {
         logger.error("Error rejecting leave request: {}", leaveUuid, e);

		  throw new LeaveRequestNotFoundException(HrManagementEnum.LEAVE_REQUEST_NOT_FOUND);
	}
	    }
	 

	    public List<LeaveManagement> getLeaveHistory() {

	    	try {
	    	String userName = SecurityContextHolder.getContext().getAuthentication().getName();
			Employee loggedInEmployee = employeeRepoDaoImpl.findByUserName(userName);
			String userId = loggedInEmployee.getEmployeeUuid();
            logger.info("Fetching leave history for employee: {}", userId);

	        return leaveRepoDaoImpl.findLeaveHistoryByEmployeeUuid(userId);
	    } catch (Exception e) {
            logger.error("Error fetching leave history", e);

            throw new InternalServerError(HrManagementEnum.INTERNAL_SERVER_ERROR);
        }
	    }
}
