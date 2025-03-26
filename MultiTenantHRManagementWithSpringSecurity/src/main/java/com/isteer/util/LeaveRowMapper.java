package com.isteer.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.isteer.dto.LeaveResponseDto;
import com.isteer.entity.LeaveManagement;

public class LeaveRowMapper implements RowMapper<LeaveManagement> {

	@Override
	public LeaveManagement mapRow(ResultSet rs, int rowNum) throws SQLException {
		LeaveManagement leave = new LeaveManagement();
        leave.setLeaveUuid(rs.getString("leave_uuid"));
        leave.setEmployeeId(rs.getString("employee_id"));
        leave.setDepartmentId(rs.getString("department_id"));
        leave.setStartDate(rs.getDate("start_date"));
        leave.setEndDate(rs.getDate("end_date"));
        leave.setReason(rs.getString("reason"));
        leave.setStatus(rs.getString("status"));
        leave.setAppliedAt(rs.getTimestamp("applied_at"));
        leave.setApprovedBy(rs.getString("approved_by"));
        leave.setApprovedAt(rs.getTimestamp("approved_at"));
        leave.setUpdatedAt(rs.getTimestamp("updated_at"));
        return leave;
	}



}
