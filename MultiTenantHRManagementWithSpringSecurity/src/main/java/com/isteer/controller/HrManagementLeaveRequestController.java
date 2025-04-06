package com.isteer.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

	private static final Logger logger = LogManager.getLogger(HrManagementLeaveRequestController.class);

	@Autowired
	HrManagementLeaveService service;

	@Autowired
	MailSenderService mailSenderService;

	@Autowired
	MailTemplateService mailTemplateService;

	@PreAuthorize("@authService.hasPermission()")
	@PostMapping("leave")
	public ResponseEntity<?> applyLeave(@Valid @RequestBody LeaveRequestDto leaveRequestDto) {

		logger.info("Attempting to apply leave for employee: {}", leaveRequestDto.getEmployeeUuid());
		int result = service.applyLeave(leaveRequestDto);

		if (result > 0) {
			logger.info("Leave application successful for employee: {}", leaveRequestDto.getEmployeeUuid());

			StatusMessageDto message = new StatusMessageDto(HrManagementEnum.Leave_application_success.getStatusCode(),
					HrManagementEnum.Leave_application_success.getStatusMessage());
			return ResponseEntity.status(HttpStatus.OK).body(message);
		} else {
			logger.error("Leave application failed for employee: {}", leaveRequestDto.getEmployeeUuid());

			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.LEAVE_APPLICATION_FAILED.getStatusCode(),
					HrManagementEnum.LEAVE_APPLICATION_FAILED.getStatusMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}
	}

	@PreAuthorize("@authService.hasPermission()") // Ensure only department heads can access
	@GetMapping("/leaves")
	public ResponseEntity<?> getAllLeaves(@RequestParam String status) {
		logger.info("Fetching leave requests with status: {}", status);

		// Fetch the list of leave requests filtered by status
		List<LeaveManagement> leaveList = service.getAllLeavesByStatus(status);
		if (leaveList.isEmpty()) {
			logger.warn("No leave requests found for status: {}", status);

			ErrorMessageDto error = new ErrorMessageDto();
			error.setErrorMessage("No leave requests found for the given status.");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
		}
		logger.info("Successfully retrieved {} leave requests with status: {}", leaveList.size(), status);

		return ResponseEntity.ok(leaveList);
	}

	@PreAuthorize("@authService.hasPermission()")
	@PutMapping("/approve")
	public ResponseEntity<?> approveLeaveRequest(@RequestParam String leaveUuid) {

		boolean isApproved = service.approveLeaveRequest(leaveUuid);

		if (isApproved) {
			logger.info("Leave request approved for UUID: {}", leaveUuid);

			MailDto maileSender = mailTemplateService.approveOrRejectLeaveRequest(leaveUuid);
			mailSenderService.sendLeaveApprovalEmail(maileSender);
			StatusMessageDto message = new StatusMessageDto(HrManagementEnum.LEAVE_APPROVAL_SUCCESS.getStatusCode(),
					HrManagementEnum.LEAVE_APPROVAL_SUCCESS.getStatusMessage());

			return ResponseEntity.status(HttpStatus.OK).body(message);
		} else {
			logger.error("Leave request approval failed for UUID: {}", leaveUuid);

			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.LEAVE_APPROVAL_FAILED.getStatusCode(),
					HrManagementEnum.LEAVE_APPROVAL_FAILED.getStatusMessage());
			logger.error("Error while approving leave request for UUID: {}", leaveUuid);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}

	}

	@PreAuthorize("@authService.hasPermission()")
	@PutMapping("/reject")
	public ResponseEntity<?> rejectLeaveRequest(@RequestParam String leaveUuid) {
		logger.info("Attempting to reject leave request for UUID: {}", leaveUuid);

		boolean isRejected = service.rejectLeaveRequest(leaveUuid);

		if (isRejected) {
			logger.info("Leave request rejected for UUID: {}", leaveUuid);

			MailDto maileSender = mailTemplateService.approveOrRejectLeaveRequest(leaveUuid);
			mailSenderService.sendLeaveRejectEmail(maileSender);

			StatusMessageDto message = new StatusMessageDto(HrManagementEnum.LEAVE_REJECTED_SUCCESS.getStatusCode(),
					HrManagementEnum.LEAVE_REJECTED_SUCCESS.getStatusMessage());
			return ResponseEntity.status(HttpStatus.OK).body(message);
		} else {
			logger.error("Leave request rejection failed for UUID: {}", leaveUuid);

			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.LEAVE_REJECTION_FAILED.getStatusCode(),
					HrManagementEnum.LEAVE_REJECTION_FAILED.getStatusMessage());
			logger.error("Error while rejecting leave request for UUID: {}", leaveUuid);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}
	}

	@GetMapping("/history")
	public ResponseEntity<?> getLeaveHistory() {
		logger.info("Fetching leave history for employee");

		List<LeaveManagement> leaveHistory = service.getLeaveHistory();

		if (leaveHistory.isEmpty()) {
			logger.warn("No leave history found for the employee.");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No leave history found for the given employee.");
		}
		logger.info("Successfully retrieved {} leave History: {}", leaveHistory.size());

		return ResponseEntity.status(HttpStatus.OK).body(leaveHistory);

	}

}
