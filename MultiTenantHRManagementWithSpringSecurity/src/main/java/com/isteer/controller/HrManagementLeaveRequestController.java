package com.isteer.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.isteer.dto.ErrorMessageDto;
import com.isteer.dto.LeaveRequestDto;
import com.isteer.dto.MailDto;
import com.isteer.dto.StatusMessageDto;
import com.isteer.entity.LeaveManagement;
import com.isteer.enums.HrManagementEnum;
import com.isteer.mail.service.MailSenderService;
import com.isteer.mail.service.MailTemplateService;
import com.isteer.service.HrManagementLeaveService;

import jakarta.validation.Valid;

@RequestMapping("/hrManagement")
@RestController
public class HrManagementLeaveRequestController {

	@Autowired
	HrManagementLeaveService service;
	
	@Autowired
	MailSenderService  mailSenderService;
	
	@Autowired
	MailTemplateService mailTemplateService;
	
	
	
	@PreAuthorize("@authService.hasPermission()")
	@PostMapping("leave")
	public ResponseEntity<?> applyLeave(@Valid @RequestBody LeaveRequestDto leaveRequestDto) {
	    // Validate endDate, ensure it's not in the past
	    if (leaveRequestDto.getEndDate().isBefore(LocalDate.now())) {
	        ErrorMessageDto error = new ErrorMessageDto(
	            HrManagementEnum.INVALID_LEAVE_DATE.getStatusCode(),
	            HrManagementEnum.INVALID_LEAVE_DATE.getStatusMessage()
	        );
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	    }

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

	 
//	@PreAuthorize("@authService.hasPermission()")
//	 @GetMapping("/leaves")
//	    public ResponseEntity<?> getAllLeaves() {
//	        List<LeaveManagement> leaveList = service.getAllLeaves();
//	        if (leaveList.isEmpty()) {
//	            ErrorMessageDto error = new ErrorMessageDto();
//	            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
//	        }
//	        return ResponseEntity.ok(leaveList);
//	    }
	
	@PreAuthorize("@authService.hasPermission()")  // Ensure only department heads can access
	@GetMapping("/leaves")
	public ResponseEntity<?> getAllLeaves(@RequestParam String status) {
	    // Fetch the list of leave requests filtered by status
	    List<LeaveManagement> leaveList = service.getAllLeavesByStatus(status);
	    if (leaveList.isEmpty()) {
	        ErrorMessageDto error = new ErrorMessageDto();
	        error.setErrorMessage("No leave requests found for the given status.");
	        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
	    }
	    return ResponseEntity.ok(leaveList);
	}

	 
	
	
	@PreAuthorize("@authService.hasPermission()")
	@PutMapping("/approve")
	public ResponseEntity<?> approveLeaveRequest(@RequestParam String leaveUuid) { 
	    try {
	    	  
	        // Call the service to approve the leave request
	        boolean isApproved = service.approveLeaveRequest(leaveUuid);
	        
	        if (isApproved) {
	         MailDto maileSender = mailTemplateService.approveOrRejectLeaveRequest(leaveUuid);
	         mailSenderService.sendLeaveApprovalEmail(maileSender);
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

	 
	    @PreAuthorize("@authService.hasPermission()")
	    @PutMapping("/reject")
	    public ResponseEntity<?> rejectLeaveRequest(@RequestParam String leaveUuid) {
	        try {
	            // Call the service to approve the leave request
	            boolean isApproved = service.rejectLeaveRequest(leaveUuid);
	            
	            if (isApproved) {
	            	 MailDto maileSender = mailTemplateService.approveOrRejectLeaveRequest(leaveUuid);
	    	         mailSenderService.sendLeaveRejectEmail(maileSender);
	            	
	                StatusMessageDto message = new StatusMessageDto(HrManagementEnum.LEAVE_REJECTED_SUCCESS.getStatusCode(),
	                        HrManagementEnum.LEAVE_REJECTED_SUCCESS.getStatusMessage());
	                return ResponseEntity.status(HttpStatus.OK).body(message);
	            } else {
	                // If leave request could not be approved
	                ErrorMessageDto error = new ErrorMessageDto(
	                        HrManagementEnum.LEAVE_REJECTION_FAILED.getStatusCode(),
	                        HrManagementEnum.LEAVE_REJECTION_FAILED.getStatusMessage()
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
	    public ResponseEntity<?> getLeaveHistory() {
	        try {
	            // Call the service to fetch leave history
	            List<LeaveManagement> leaveHistory = service.getLeaveHistory();

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
