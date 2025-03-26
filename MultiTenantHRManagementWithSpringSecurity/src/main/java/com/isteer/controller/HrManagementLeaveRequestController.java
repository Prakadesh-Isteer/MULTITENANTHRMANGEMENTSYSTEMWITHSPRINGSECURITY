package com.isteer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.isteer.dto.ErrorMessageDto;
import com.isteer.dto.LeaveRequestDto;
import com.isteer.dto.StatusMessageDto;
import com.isteer.entity.LeaveManagement;
import com.isteer.enums.HrManagementEnum;
import com.isteer.service.HrManagementLeaveService;

import jakarta.validation.Valid;

@RequestMapping("/hrManagement")
@RestController
public class HrManagementLeaveRequestController {

	@Autowired
	HrManagementLeaveService service;
		
	 @PostMapping("leave")
	    public ResponseEntity<?> applyLeave(@RequestParam String employeeUuid, @Valid @RequestBody LeaveRequestDto leaveRequestDto) {
		 leaveRequestDto.setEmployeeId(employeeUuid); 
	        // Get the result from service (this is now an int)
	        int result = service.applyLeave(leaveRequestDto);
	        
	        if (result > 0) {  // If rows were affected, leave application was successful
	            StatusMessageDto message = new StatusMessageDto(HrManagementEnum.Leave_application_success.getStatusCode(),
	                    HrManagementEnum.Leave_application_success.getStatusMessage());
	            return ResponseEntity.status(HttpStatus.OK).body(message);
	        } else {
	            // If result is 0 or less, something went wrong
	            ErrorMessageDto error = new ErrorMessageDto(
	                HrManagementEnum.LEAVE_APPLICATION_FAILED.getStatusCode(),
	                HrManagementEnum.LEAVE_APPLICATION_FAILED.getStatusMessage()
	            );
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	        }
	    }
	 
	 
	 @GetMapping("/leaves")
	    public ResponseEntity<?> getAllLeaves() {
	        List<LeaveManagement> leaveList = service.getAllLeaves();
	        if (leaveList.isEmpty()) {
	            ErrorMessageDto error = new ErrorMessageDto();
	            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
	        }
	        return ResponseEntity.ok(leaveList);
	    }
	 
	 
	  // Endpoint to approve a leave request
	    @PutMapping("/approve")
	    public ResponseEntity<?> approveLeaveRequest(@RequestParam String leaveUuid, @RequestParam String approvedBy) {
	        try {
	            // Call the service to approve the leave request
	            boolean isApproved = service.approveLeaveRequest(leaveUuid, approvedBy);
	            
	            if (isApproved) {
	                StatusMessageDto message = new StatusMessageDto(HrManagementEnum.LEAVE_APPROVAL_SUCCESS.getStatusCode(),
	                        HrManagementEnum.LEAVE_APPROVAL_SUCCESS.getStatusMessage());
	                return ResponseEntity.status(HttpStatus.OK).body(message);
	            } else {
	                // If leave request could not be approved
	                ErrorMessageDto error = new ErrorMessageDto(
	                        HrManagementEnum.LEAVE_APPROVAL_FAILED.getStatusCode(),
	                        HrManagementEnum.LEAVE_APPROVAL_FAILED.getStatusMessage()
	                );
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request");
	        }
	    }
	    
	    @PutMapping("/reject")
	    public ResponseEntity<?> rejectLeaveRequest(@RequestParam String leaveUuid, @RequestParam String approvedBy) {
	        try {
	            // Call the service to approve the leave request
	            boolean isApproved = service.rejectLeaveRequest(leaveUuid, approvedBy);
	            
	            if (isApproved) {
	                StatusMessageDto message = new StatusMessageDto(HrManagementEnum.LEAVE_APPROVAL_SUCCESS.getStatusCode(),
	                        HrManagementEnum.LEAVE_APPROVAL_SUCCESS.getStatusMessage());
	                return ResponseEntity.status(HttpStatus.OK).body(message);
	            } else {
	                // If leave request could not be approved
	                ErrorMessageDto error = new ErrorMessageDto(
	                        HrManagementEnum.LEAVE_APPROVAL_FAILED.getStatusCode(),
	                        HrManagementEnum.LEAVE_APPROVAL_FAILED.getStatusMessage()
	                );
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request");
	        }
	    }
	    
	    
	 // Endpoint to get the leave history for an employee
	    @GetMapping("/history")
	    public ResponseEntity<?> getLeaveHistory(@RequestParam String employeeUuid) {
	        try {
	            // Call the service to fetch leave history
	            List<LeaveManagement> leaveHistory = service.getLeaveHistory(employeeUuid);

	            if (leaveHistory.isEmpty()) {
	                // If no leave history found for the employee
	                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No leave history found for the given employee.");
	            }

	            return ResponseEntity.status(HttpStatus.OK).body(leaveHistory);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching the leave history.");
	        }
	    }
	 
}
