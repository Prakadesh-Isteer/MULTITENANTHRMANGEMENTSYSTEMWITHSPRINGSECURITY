package com.isteer.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.isteer.entity.LeaveManagement;

public class LeaveRowMapper implements RowMapper<LeaveManagement> {
    @Override
    public LeaveManagement mapRow(ResultSet rs, int rowNum) throws SQLException {
        LeaveManagement leave = new LeaveManagement();
        leave.setLeaveUuid(rs.getString("leave_uuid"));
        leave.setEmployeeUuid(rs.getString("employee_id"));
        leave.setDepartmentUuid(rs.getString("department_id"));
        leave.setStartDate(rs.getDate("start_date"));
        leave.setEndDate(rs.getDate("end_date"));
        leave.setReason(rs.getString("reason"));
        leave.setStatus(rs.getString("status"));
        leave.setAppliedAt(rs.getTimestamp("applied_at").toLocalDateTime());
        leave.setApprovedBy(rs.getString("approved_or_rejected_by"));
        leave.setApprovedAt(rs.getTimestamp("approved_or_rejected_at") != null ? 
                                      rs.getTimestamp("approved_or_rejected_at") : null);
        return leave;
    }
}




