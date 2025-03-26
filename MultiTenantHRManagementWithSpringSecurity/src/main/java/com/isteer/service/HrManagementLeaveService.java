package com.isteer.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isteer.dto.LeaveRequestDto;
import com.isteer.dto.LeaveResponseDto;
import com.isteer.entity.LeaveManagement;
import com.isteer.enums.HrManagementEnum;
import com.isteer.exception.EmployeeNotFoundException;
import com.isteer.exception.LeaveRequestNotFoundException;
import com.isteer.repository.LeaveRepoDaoImpl;

@Service
public class HrManagementLeaveService {

	@Autowired
	LeaveRepoDaoImpl repo;

	public int applyLeave(LeaveRequestDto leaveRequestDto) {
		// Apply for leave by calling the repository and return the result (number of
		// rows affected)
		try {
			return repo.applyLeave(leaveRequestDto); // Now returns an int instead of LeaveResponseDto
		} catch (EmployeeNotFoundException ex) {
			// Handle exception or let it propagate
			throw ex; // Re-throw exception if necessary
		}
	}

	@Transactional
	public List<LeaveManagement> getAllLeaves() {
		return repo.getAllLeaves();
	}
	

//	    

	 // Approve leave request by delegating the logic to the repository layer
	 @Transactional
	    public boolean approveLeaveRequest(String leaveUuid, String approvedBy) {
	        // Delegate the logic to the repository
	        return repo.approveLeaveRequest(leaveUuid, approvedBy);
	    }
	 
	 @Transactional
	    public boolean rejectLeaveRequest(String leaveUuid, String approvedBy) {
	        // Delegate the logic to the repository
	        return repo.rejectLeaveRequest(leaveUuid, approvedBy);
	    }
	 
	// Fetch leave history for an employee
	    public List<LeaveManagement> getLeaveHistory(String employeeUuid) {
	        return repo.findLeaveHistoryByEmployeeUuid(employeeUuid);
	    }
}
