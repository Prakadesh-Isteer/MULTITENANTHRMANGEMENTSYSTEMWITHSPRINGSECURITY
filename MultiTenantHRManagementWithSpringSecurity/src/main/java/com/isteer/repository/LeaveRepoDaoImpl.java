package com.isteer.repository;

import java.sql.Timestamp;
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
import com.isteer.exception.EmployeeIdNullException;
import com.isteer.exception.EmployeeNotFoundException;
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
	public Optional<Employee> findById(String userId) {
		String sql = "SELECT employee_uuid, role_id, tenant_id, department_id, userName, password, first_name, last_name, email, phone, address, date_of_joining, job_title FROM employee WHERE employee_status = :status AND employee_uuid = :employeeId";
		SqlParameterSource param = new MapSqlParameterSource().addValue("employeeId", userId).addValue("status", 1);

		try {
			Employee employee = template.queryForObject(sql, param, new EmployeeRowMapper());
			return Optional.ofNullable(employee);
		} catch (EmptyResultDataAccessException e) {
			// Throw custom exception with error code and message
			throw new EmployeeNotFoundException(HrManagementEnum.EMPLOYEE_VALID_NOT_FOUND);
		}
	}

	@Transactional
	@Override
	public int applyLeave(LeaveRequestDto leaveRequestDto) {
		// Check if the employee exists
		Optional<Employee> existingUser = findById(leaveRequestDto.getEmployeeId());

		if (!existingUser.isPresent()) {
			// Employee not found, return 0 or handle as per your logic
			return 0; // No employee found, return 0 to indicate failure
		}

		// Generate a unique leave UUID
		UUID uuid = UUID.randomUUID();
		String leaveUuid = uuid.toString();

		try {
			// Query to get employee's department_id
			String getDepartmentIdQuery = "SELECT department_id FROM employee WHERE employee_uuid = :employeeId LIMIT 1";
			String departmentId = template.queryForObject(getDepartmentIdQuery,
					new MapSqlParameterSource().addValue("employeeId", leaveRequestDto.getEmployeeId()), String.class);

			// Insert leave request into the leaves table with department_id from employee
			String insertLeaveQuery = "INSERT INTO leaves (leave_uuid, employee_id, department_id, start_date, end_date, reason, status, applied_at) "
					+ "VALUES (:leaveUuid, :employeeId, :departmentId, :startDate, :endDate, :reason, 'PENDING', CURRENT_TIMESTAMP)";

			SqlParameterSource params = new MapSqlParameterSource().addValue("leaveUuid", leaveUuid)
					.addValue("employeeId", leaveRequestDto.getEmployeeId()).addValue("departmentId", departmentId)
					.addValue("startDate", leaveRequestDto.getStartDate())
					.addValue("endDate", leaveRequestDto.getEndDate()).addValue("reason", leaveRequestDto.getReason());

			int result = template.update(insertLeaveQuery, params); // This will return the number of rows affected

			return result; // Return the result of the update operation (number of rows affected)
		} catch (Exception e) {
			e.printStackTrace();
		}

		// If any error occurs during the insertion, throw custom exception
		throw new InsertionFailedException(HrManagementEnum.Insertion_failed_Exception);
	}

	public List<LeaveManagement> getAllLeaves() {
		String sql = "SELECT leave_uuid, employee_id, department_id, start_date, end_date, reason, status, "
				+ "applied_at, approved_by, approved_at, updated_at " + "FROM leaves";
		return template.query(sql, new LeaveRowMapper());
	}

//	// Find leave request by UUID
//	public Optional<LeaveManagement> findLeaveByUuid(String leaveUuid) {
//		String sql = "SELECT leave_uuid, employee_id, department_id, start_date, end_date, reason, status, "
//				+ "applied_at, approved_by, approved_at, updated_at " + "FROM leaves WHERE leave_uuid = :leaveUuid";
//
//		SqlParameterSource params = new MapSqlParameterSource("leaveUuid", leaveUuid);
//		try {
//			LeaveManagement leave = template.queryForObject(sql, params, new LeaveRowMapper());
//			return Optional.ofNullable(leave);
//		} catch (EmptyResultDataAccessException e) {
//			return Optional.empty(); // No leave found with the given UUID
//		}
//	}
//
//	// Save the updated leave request
//	public void save(LeaveManagement leave) {
//		String updateSql = "UPDATE leaves SET status = :status, approved_by = :approvedBy, approved_at = :approvedAt "
//				+ "WHERE leave_uuid = :leaveUuid";
//
//		SqlParameterSource params = new MapSqlParameterSource().addValue("status", leave.getStatus())
//				.addValue("approvedBy", leave.getApprovedBy()).addValue("approvedAt", leave.getApprovedAt())
//				.addValue("leaveUuid", leave.getLeaveUuid());
//
//		template.update(updateSql, params); // Update the leave record in the database
//	}
	

	  // Find leave request by UUID
    public Optional<LeaveManagement> findLeaveByUuid(String leaveUuid) {
        String sql = "SELECT leave_uuid, employee_id, department_id, start_date, end_date, reason, status, "
                + "applied_at, approved_by, approved_at, updated_at FROM leaves WHERE leave_uuid = :leaveUuid";

        SqlParameterSource params = new MapSqlParameterSource("leaveUuid", leaveUuid);
        try {
            LeaveManagement leave = template.queryForObject(sql, params, new LeaveRowMapper());
            return Optional.ofNullable(leave);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty(); // No leave found with the given UUID
        }
    }

    // Approve leave request and update in the database
    public boolean approveLeaveRequest(String leaveUuid, String approvedBy) {
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

        // Update the leave status to APPROVED and set the approvedBy and approvedAt fields
        leave.setStatus("APPROVED");
        leave.setApprovedBy(approvedBy);
        leave.setApprovedAt(new Timestamp(System.currentTimeMillis()));  // Set the approval timestamp

        // Save the updated leave record in the database
        save(leave);

        return true;  // Successfully approved
    }

    // Save the updated leave request
    public void save(LeaveManagement leave) {
        String updateSql = "UPDATE leaves SET status = :status, approved_by = :approvedBy, approved_at = :approvedAt "
                + "WHERE leave_uuid = :leaveUuid";

        SqlParameterSource params = new MapSqlParameterSource().addValue("status", leave.getStatus())
                .addValue("approvedBy", leave.getApprovedBy()).addValue("approvedAt", leave.getApprovedAt())
                .addValue("leaveUuid", leave.getLeaveUuid());

        template.update(updateSql, params); // Update the leave record in the database
    }
    
    

    // Approve leave request and update in the database
    public boolean rejectLeaveRequest(String leaveUuid, String approvedBy) {
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

        // Update the leave status to APPROVED and set the approvedBy and approvedAt fields
        leave.setStatus("REJECTED");
        leave.setApprovedBy(approvedBy);
        leave.setApprovedAt(new Timestamp(System.currentTimeMillis()));  // Set the approval timestamp

        // Save the updated leave record in the database
        save(leave);

        return true;  // Successfully approved
    }

    // Save the updated leave request
    public void saveAs(LeaveManagement leave) {
        String updateSql = "UPDATE leaves SET status = :status, approved_by = :approvedBy, approved_at = :approvedAt "
                + "WHERE leave_uuid = :leaveUuid";

        SqlParameterSource params = new MapSqlParameterSource().addValue("status", leave.getStatus())
                .addValue("approvedBy", leave.getApprovedBy()).addValue("approvedAt", leave.getApprovedAt())
                .addValue("leaveUuid", leave.getLeaveUuid());

        template.update(updateSql, params); // Update the leave record in the database
    }
    
    
    // Fetch leave history by employee UUID
    public List<LeaveManagement> findLeaveHistoryByEmployeeUuid(String employeeUuid) {
        String sql = "SELECT leave_uuid, employee_id, department_id, start_date, end_date, reason, status, applied_at, approved_by, approved_at, updated_at "
                   + "FROM leaves WHERE employee_id = :employeeUuid";

        SqlParameterSource params = new MapSqlParameterSource("employeeUuid", employeeUuid);

        return template.query(sql, params, new LeaveRowMapper());  // Assuming LeaveRowMapper maps rows to LeaveManagement objects
    }
    
}
