package com.isteer.repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.isteer.dto.LeaveRequestDto;
import com.isteer.entity.Employee;
import com.isteer.entity.LeaveManagement;
import com.isteer.enums.HrManagementEnum;
import com.isteer.exception.InsertionFailedException;
import com.isteer.exception.LeaveRequestNotFoundException;
import com.isteer.repository.dao.LeaveRepoDao;
import com.isteer.util.EmployeeRowMapper;
import com.isteer.util.LeaveRowMapper;

@Component
public class LeaveRepoDaoImpl implements LeaveRepoDao {

	@Autowired
	NamedParameterJdbcTemplate template;

	

	@Transactional
	@Override
	public int applyLeave(LeaveRequestDto leaveRequestDto, String departmentId, LocalDate startDate) {
	    UUID uuid = UUID.randomUUID();
	    String leaveUuid = uuid.toString();

	    try {
	        // Convert LocalDate to java.sql.Date
	        java.sql.Date sqlStartDate = java.sql.Date.valueOf(startDate);
	        java.sql.Date sqlEndDate = java.sql.Date.valueOf(leaveRequestDto.getEndDate());

	        // Insert leave request into the leaves table with department_id from employee
	        String insertLeaveQuery = "INSERT INTO leaves (leave_uuid, employee_id, department_id, start_date, end_date, reason, status, applied_at) "
	                + "VALUES (:leaveUuid, :employeeId, :departmentId, :startDate, :endDate, :reason, :status, CURRENT_TIMESTAMP)";

	        SqlParameterSource params = new MapSqlParameterSource()
	                .addValue("leaveUuid", leaveUuid)
	                .addValue("status", "PENDING")
	                .addValue("employeeId", leaveRequestDto.getEmployeeUuid())
	                .addValue("departmentId", departmentId)
	                .addValue("startDate", sqlStartDate) // Pass sqlStartDate here
	                .addValue("endDate", sqlEndDate)    // Pass sqlEndDate here
	                .addValue("reason", leaveRequestDto.getReason());

	        int result = template.update(insertLeaveQuery, params); // This will return the number of rows affected

	        return result; // Return the result of the update operation (number of rows affected)
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    // If any error occurs during the insertion, throw custom exception
	    throw new InsertionFailedException(HrManagementEnum.Insertion_failed_Exception);
	}

//
//	@Transactional
//	public List<LeaveManagement> getAllLeaves() {
//		String sql = "SELECT leave_uuid, employee_id, department_id, start_date, end_date, reason, status, "
//				+ "applied_at, approved_or_rejected_by, approved_or_rejected_at " + "FROM leaves";
//		return template.query(sql, new LeaveRowMapper());
//	}
	
	
	public List<LeaveManagement> getAllLeavesByStatus(String departmentId, String status) {
//	    String sql = "SELECT l.leave_uuid, l.employee_id, l.department_id, l.start_date, l.end_date, " +
//	                 "l.reason, l.status, l.applied_at, l.approved_or_rejected_by, l.approved_or_rejected_at " +
//	                 "FROM leaves l " +
//	                 "JOIN employee e ON l.employee_id = e.employee_uuid " +
//	                 "WHERE l.department_id = :departmentId " +
//	                 "AND l.status = :status " +
//	                 "AND e.department_id = :departmentId";
		
		String sql = "SELECT l.leave_uuid, l.employee_id, l.department_id, l.start_date, l.end_date, " +
	             "l.reason, l.status, l.applied_at, l.approved_or_rejected_by, l.approved_or_rejected_at " +
	             "FROM leaves l " +
	             "WHERE l.department_id = :departmentId " +
	             "AND l.status = :status";


	    SqlParameterSource param = new MapSqlParameterSource()
	                               .addValue("departmentId", departmentId)
	                               .addValue("status", status);
	    System.out.println("Executing SQL: " + sql);
	    System.out.println("Parameters: departmentId=" + departmentId + ", status=" + status);

	    return template.query(sql, param, new LeaveRowMapper());
	}


	
	// Find leave request by UUID
	@Transactional
	public Optional<LeaveManagement> findLeaveByUuid(String leaveUuid) {
		String sql = "SELECT leave_uuid, employee_id, department_id, start_date, end_date, reason, status, "
				+ "applied_at, approved_or_rejected_by, approved_or_rejected_at FROM leaves WHERE leave_uuid = :leaveUuid";

		SqlParameterSource params = new MapSqlParameterSource("leaveUuid", leaveUuid);
		try {
			LeaveManagement leave = template.queryForObject(sql, params, new LeaveRowMapper());
			return Optional.ofNullable(leave);
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty(); // No leave found with the given UUID
		}
	}
	
		
	
	@Transactional
	public Optional<LeaveManagement> findLeaveByUuidForUserName(String leaveUuid) {
	    String sql = "SELECT l.leave_uuid, l.employee_id, l.department_id, l.start_date, l.end_date, l.reason, l.status, "
	            + "l.applied_at, l.approved_or_rejected_by, l.approved_or_rejected_at, e.userName AS employee_name "
	            + "FROM leaves l "
	            + "JOIN employee e ON e.employee_uuid = l.employee_id "
	            + "WHERE l.leave_uuid = :leaveUuid";
	    
	
	    SqlParameterSource params = new MapSqlParameterSource()
	    		.addValue("leaveUuid", leaveUuid);
	    try {
	        LeaveManagement leave = template.queryForObject(sql, params, new LeaveRowMapper());
	        return Optional.ofNullable(leave);
	    } catch (EmptyResultDataAccessException e) {
	        return Optional.empty(); // No leave found with the given UUID
	    }
	}
	
	
	public List<Employee> getHrDetails(String tenantId){
		
		String sql = "SELECT employee_uuid, role_id, tenant_id, department_id, userName, password, first_name, last_name, email, phone, address, date_of_joining, job_title FROM employee WHERE employee_status = :status AND tenant_id = :tenantId";
		SqlParameterSource param =  new MapSqlParameterSource()
				.addValue("tenantId", tenantId )
				.addValue("status", 1);
		List<Employee> wrkDetails = template.query(sql, param , new EmployeeRowMapper());
		
		return  wrkDetails;

	}
	

	// Update the leave status to 'APPROVED' and set the approved_by and approved_at
	// fields
	@Transactional
	public boolean approveLeaveRequest(String leaveUuid, String userId, String departmentId) {
		// Fetch the leave details
		Optional<LeaveManagement> leaveRequest = findLeaveByUuid(leaveUuid);

		if (!leaveRequest.isPresent()) {
			throw new LeaveRequestNotFoundException(HrManagementEnum.LEAVE_REQUEST_NOT_FOUND);
		}

		LeaveManagement leave = leaveRequest.get();

		// Check if the leave request is already approved or rejected
		if (leave.getStatus().equalsIgnoreCase("APPROVED") || leave.getStatus().equalsIgnoreCase("REJECTED")) {
			// Leave request is already processed, return false
			return false;
		}

		// Update the leave status to APPROVED and set the approvedBy and approvedAt
		// fields
		String updateSql = "UPDATE leaves SET status = :status, approved_or_rejected_by = :approvedBy, approved_or_rejected_at = :approvedAt "
				+ "WHERE leave_uuid = :leaveUuid and department_id = :departmentId";

		SqlParameterSource params = new MapSqlParameterSource().addValue("status", "APPROVED")
				.addValue("approvedBy", userId).addValue("approvedAt", new Timestamp(System.currentTimeMillis()))
				.addValue("departmentId", departmentId).addValue("leaveUuid", leaveUuid);

		// Execute the update query
		int rowsAffected = template.update(updateSql, params);

		return rowsAffected > 0; // Return true if the leave was successfully approved
	}
	
	
	
	

//Update the leave status to 'REJECT' and set the approved_by and approved_at fields
	@Transactional
	public boolean rejectLeaveRequest(String leaveUuid , String userId, String departmentId) {
		// Fetch the leave details
		Optional<LeaveManagement> leaveRequest = findLeaveByUuid(leaveUuid);

		if (!leaveRequest.isPresent()) {
			throw new LeaveRequestNotFoundException(HrManagementEnum.LEAVE_REQUEST_NOT_FOUND);
		}

		LeaveManagement leave = leaveRequest.get();

		// Check if the leave request is already approved or rejected
		if (leave.getStatus().equalsIgnoreCase("APPROVED") || leave.getStatus().equalsIgnoreCase("REJECTED")) {
			// Leave request is already processed, return false
			return false;
		}

		// Update the leave status to APPROVED and set the approvedBy and approvedAt
		// fields
		String updateSql = "UPDATE leaves SET status = :status, approved_or_rejected_by = :approvedBy, approved_or_rejected_at = :approvedAt "
				+ "WHERE leave_uuid = :leaveUuid and department_id = :departmentId";

		SqlParameterSource params = new MapSqlParameterSource().addValue("status", "REJECTED")
//         .addValue("approvedBy", hodEmployeeUuid)
				.addValue("approvedBy", userId).addValue("approvedAt", new Timestamp(System.currentTimeMillis()))
				.addValue("departmentId", departmentId).addValue("leaveUuid", leaveUuid);


		// Execute the update query
		int rowsAffected = template.update(updateSql, params);

		return rowsAffected > 0; // Return true if the leave was successfully approved
	}
	
	
	

	// Fetch leave history by employee UUID
	@Transactional
	public List<LeaveManagement> findLeaveHistoryByEmployeeUuid(String userId) {
		String sql = "SELECT leave_uuid, employee_id, department_id, start_date, end_date, reason, status, applied_at, approved_or_rejected_by, approved_or_rejected_at "
				+ "FROM leaves WHERE employee_id = :employeeUuid";

		SqlParameterSource params = new MapSqlParameterSource("employeeUuid", userId);

		return template.query(sql, params, new LeaveRowMapper()); // Assuming LeaveRowMapper maps rows to
																	// LeaveManagement objects
	}



}
