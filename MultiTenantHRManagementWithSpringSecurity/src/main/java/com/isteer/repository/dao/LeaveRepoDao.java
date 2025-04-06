package com.isteer.repository.dao;

import java.time.LocalDate;
import java.util.List;

import com.isteer.dto.LeaveRequestDto;
import com.isteer.entity.Employee;
import com.isteer.entity.LeaveManagement;

public interface LeaveRepoDao {
	
	public int applyLeave(LeaveRequestDto leaveRequestDto, String departmentId, LocalDate startDate);

	public List<LeaveManagement> getAllLeavesByStatus(String departmentId, String status);
	
	public List<Employee> getHrDetails(String tenantId);
	
	public boolean approveLeaveRequest(String leaveUuid, String userId, String departmentId);
	
	public boolean rejectLeaveRequest(String leaveUuid, String userId, String departmentId);
	
	public List<LeaveManagement> findLeaveHistoryByEmployeeUuid(String userId) ;
}
