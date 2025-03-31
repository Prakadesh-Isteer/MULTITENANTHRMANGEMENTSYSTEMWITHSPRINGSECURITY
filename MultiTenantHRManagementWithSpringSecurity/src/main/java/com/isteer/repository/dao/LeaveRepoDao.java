package com.isteer.repository.dao;

import java.time.LocalDate;

import com.isteer.dto.LeaveRequestDto;
import com.isteer.dto.LeaveResponseDto;

public interface LeaveRepoDao {
	
	public int applyLeave(LeaveRequestDto leaveRequestDto, String departmentId, LocalDate startDate);

}
