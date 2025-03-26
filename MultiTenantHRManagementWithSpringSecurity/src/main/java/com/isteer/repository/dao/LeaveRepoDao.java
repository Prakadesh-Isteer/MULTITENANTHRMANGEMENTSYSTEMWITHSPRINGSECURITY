package com.isteer.repository.dao;

import com.isteer.dto.LeaveRequestDto;
import com.isteer.dto.LeaveResponseDto;

public interface LeaveRepoDao {
	
	public int applyLeave(LeaveRequestDto leaveRequestDto);

}
