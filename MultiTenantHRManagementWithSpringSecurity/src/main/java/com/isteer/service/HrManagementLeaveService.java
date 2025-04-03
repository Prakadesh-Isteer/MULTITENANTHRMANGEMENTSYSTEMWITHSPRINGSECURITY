package com.isteer.service;

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
import com.isteer.exception.EmployeeNotFoundException;
import com.isteer.repository.EmployeeRepoDaoImpl;
import com.isteer.repository.LeaveRepoDaoImpl;

@Service
public class HrManagementLeaveService {

	@Autowired
	EmployeeRepoDaoImpl employeeRepoDaoImpl;
	
	@Autowired
	LeaveRepoDaoImpl leaveRepoDaoImpl;


	
	@Transactional

	public int applyLeave(LeaveRequestDto leaveRequestDto) {
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
         throw new DateBeforeInvaildException(HrManagementEnum.Date_Exception);
	    }

	    // Now we call the repository to apply the leave
	    try {
	        return leaveRepoDaoImpl.applyLeave(leaveRequestDto, departmentId, startDate);
	    } catch (EmployeeNotFoundException ex) {
	        // Handle exception or let it propagate
	        throw ex;
	    }
	}


	
	@Transactional
	public List<LeaveManagement> getAllLeavesByStatus(String status) {
	    // Get the logged-in employee (department head)
	    String userName = SecurityContextHolder.getContext().getAuthentication().getName();
	    Employee loggedInEmployee = employeeRepoDaoImpl.findByUserName(userName);
	    
	    // Get the department ID of the department head
	    String departmentId = loggedInEmployee.getDepartmentUuid();
	    
	    
	    
	    // Pass the department ID and status to the repository layer to query leaves
	    return leaveRepoDaoImpl.getAllLeavesByStatus(departmentId, status);
	}

	

	
	@Transactional
	public boolean approveLeaveRequest(String leaveUuid) {
	    // Call the repository to approve the leave request
//		System.out.println(SecurityContextHolder.getContext().getAuthentication());
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		Employee loggedInEmployee = employeeRepoDaoImpl.findByUserName(userName);
		String departmentId = loggedInEmployee.getDepartmentUuid();
		String userId = loggedInEmployee.getEmployeeUuid();
	    return leaveRepoDaoImpl.approveLeaveRequest(leaveUuid, userId, departmentId);

	}

	 
	 @Transactional
	    public boolean rejectLeaveRequest(String leaveUuid) {
		 String userName = SecurityContextHolder.getContext().getAuthentication().getName();
			Employee loggedInEmployee = employeeRepoDaoImpl.findByUserName(userName);
			String departmentId = loggedInEmployee.getDepartmentUuid();
			String userId = loggedInEmployee.getEmployeeUuid();
		 return leaveRepoDaoImpl.rejectLeaveRequest(leaveUuid, userId, departmentId);
	    }
	 
	// Fetch leave history for an employee
	    public List<LeaveManagement> getLeaveHistory() {
	    	String userName = SecurityContextHolder.getContext().getAuthentication().getName();
			Employee loggedInEmployee = employeeRepoDaoImpl.findByUserName(userName);
			String userId = loggedInEmployee.getEmployeeUuid();
	        return leaveRepoDaoImpl.findLeaveHistoryByEmployeeUuid(userId);
	    }
}
