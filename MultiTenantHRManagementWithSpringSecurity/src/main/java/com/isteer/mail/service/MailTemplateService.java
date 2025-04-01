package com.isteer.mail.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.isteer.dto.MailDto;
import com.isteer.entity.Employee;
import com.isteer.entity.LeaveManagement;
import com.isteer.entity.Roles;
import com.isteer.enums.HrManagementEnum;
import com.isteer.exception.EmployeeIdNullException;
import com.isteer.exception.LeaveRequestNotFoundException;
import com.isteer.repository.EmployeeRepoDaoImpl;
import com.isteer.repository.LeaveRepoDaoImpl;

@Component
public class MailTemplateService {

	@Autowired
	LeaveRepoDaoImpl leaveRepoDaoImpl;

	@Autowired
	EmployeeRepoDaoImpl employeeRepoDaoImpl;

	@Autowired
	JavaMailSender javaMailSender;

	public String buildLeaveApprovalEmail(MailDto contents) {
		
				
		String mailTemplate = "<!DOCTYPE html>" +
			    "<html lang=\"en\">" +
			    "<head>" +
			    "    <meta charset=\"UTF-8\">" +
			    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
			    "    <title>Leave Approval Notification</title>" +
			    "    <style>" +
			    "        /* Base styles */" +
			    "        body {" +
			    "            font-family: 'Arial', sans-serif;" +
			    "            margin: 0;" +
			    "            padding: 0;" +
			    "            background-color: #f4f4f4;" +
			    "            color: #333333;" +
			    "        }" +
			    "        table {" +
			    "            width: 100%;" +
			    "            padding: 20px 0;" +
			    "        }" +
			    "        /* Center the email container */" +
			    "        .email-container {" +
			    "            max-width: 600px;" +
			    "            margin: 0 auto;" +
			    "            background-color: #ffffff;" +
			    "            border-radius: 8px;" +
			    "            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);" +
			    "            animation: fadeIn 1s ease-in;" +
			    "        }" +
			    "        /* Header styles */" +
			    "        .email-header {" +
			    "            background-color: #2C3E50;" +
			    "            color: #F39C12;" +
			    "            text-align: center;" +
			    "            padding: 20px;" +
			    "            border-top-left-radius: 8px;" +
			    "            border-top-right-radius: 8px;" +
			    "        }" +
			    "        .email-header h2 {" +
			    "            margin: 0;" +
			    "            font-size: 28px;" +
			    "            font-weight: 600;" +
			    "        }" +
			    "        /* Content Styles */" +
			    "        .email-content {" +
			    "            padding: 20px;" +
			    "            font-size: 16px;" +
			    "            color: #555555;" +
			    "            line-height: 1.6;" +
			    "        }" +
			    "        .email-content p {" +
			    "            margin: 0 0 10px;" +
			    "        }" +
			    "        .details-table td {" +
			    "            padding: 12px;" +
			    "            background-color: #ECF0F1;" +
			    "            border: 1px solid #BDC3C7;" +
			    "            text-align: left;" +
			    "        }" +
			    "        .details-table td strong {" +
			    "            color: #2C3E50;" +
			    "        }" +
			    "        /* Footer styles */" +
			    "        .email-footer {" +
			    "            background-color: #2C3E50;" +
			    "            color: #BDC3C7;" +
			    "            text-align: center;" +
			    "            padding: 20px;" +
			    "            font-size: 12px;" +
			    "            border-bottom-left-radius: 8px;" +
			    "            border-bottom-right-radius: 8px;" +
			    "        }" +
			    "        /* Button hover effect */" +
			    "        .btn {" +
			    "            background-color: #F39C12;" +
			    "            color: white;" +
			    "            padding: 10px 20px;" +
			    "            text-align: center;" +
			    "            border-radius: 4px;" +
			    "            text-decoration: none;" +
			    "            display: inline-block;" +
			    "            transition: background-color 0.3s ease;" +
			    "        }" +
			    "        .btn:hover {" +
			    "            background-color: #D35400;" +
			    "        }" +
			    "        /* Fade-in animation */" +
			    "        @keyframes fadeIn {" +
			    "            0% { opacity: 0; }" +
			    "            100% { opacity: 1; }" +
			    "        }" +
			    "    </style>" +
			    "</head>" +
			    "<body>" +
			    "    <table>" +
			    "        <tr>" +
			    "            <td align=\"center\">" +
			    "                <div class=\"email-container\">" +
			    "                    <div class=\"email-header\">" +
			    "                        <h2>Leave Approval Notification</h2>" +
			    "                    </div>" +
			    "                    <div class=\"email-content\">" +
			    "                        <p>Dear <strong>" + contents.getFirstName() + " " + contents.getLastName() + "</strong>,</p>" +
			    "                        <p>We are pleased to inform you that your leave request has been approved. Below are the details of your leave:</p>" +
			    "                        <table class=\"details-table\" width=\"100%\" cellpadding=\"5\" cellspacing=\"0\">" +
			    "                            <tr>" +
			    "                                <td><strong>Leave Type:</strong></td>" +
			    "                                <td>" + contents.getLeaveType() + "</td>" +
			    "                            </tr>" +
			    "                            <tr>" +
			    "                                <td><strong>Start Date:</strong></td>" +
			    "                                <td>" + contents.getStartDate() + "</td>" +
			    "                            </tr>" +
			    "                            <tr>" +
			    "                                <td><strong>End Date:</strong></td>" +
			    "                                <td>" + contents.getEndDate() + "</td>" +
			    "                            </tr>" +
			    "                        </table>" +
			    "                        <p>If you have any questions or concerns, feel free to contact HR.</p>" +
			    "                        <p>Regards,</p>" +
			    "                        <p>HR Team</p>" +
			    "                        <a href=\"https://www.linkedin.com/in/anil-kumar-a525337\" class=\"btn\">Contact HR</a>" +
			    "                    </div>" +
			    "                    <div class=\"email-footer\">" +
			    "                        <p>&copy; 2025 MultiTenantHRManagement. All Rights Reserved.</p>" +
			    "                    </div>" +
			    "                </div>" +
			    "            </td>" +
			    "        </tr>" +
			    "    </table>" +
			    "</body>" +
			    "</html>";

		return mailTemplate;

	}
	
	
	
	public String buildLeaveRejectEmail(MailDto contents) {
		String mailTemplate = "<!DOCTYPE html>" +
			    "<html lang=\"en\">" +
			    "<head>" +
			    "    <meta charset=\"UTF-8\">" +
			    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
			    "    <title>Leave Rejection Notification</title>" +
			    "    <style>" +
			    "        /* Base styles */" +
			    "        body {" +
			    "            font-family: 'Arial', sans-serif;" +
			    "            margin: 0;" +
			    "            padding: 0;" +
			    "            background-color: #f4f4f4;" +
			    "            color: #333333;" +
			    "        }" +
			    "        table {" +
			    "            width: 100%;" +
			    "            padding: 20px 0;" +
			    "        }" +
			    "        /* Center the email container */" +
			    "        .email-container {" +
			    "            max-width: 600px;" +
			    "            margin: 0 auto;" +
			    "            background-color: #ffffff;" +
			    "            border-radius: 8px;" +
			    "            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);" +
			    "            animation: fadeIn 1s ease-in;" +
			    "        }" +
			    "        /* Header styles for rejection */" +
			    "        .email-header {" +
			    "            background-color: #E74C3C;" +
			    "            color: #ffffff;" +
			    "            text-align: center;" +
			    "            padding: 20px;" +
			    "            border-top-left-radius: 8px;" +
			    "            border-top-right-radius: 8px;" +
			    "        }" +
			    "        .email-header h2 {" +
			    "            margin: 0;" +
			    "            font-size: 28px;" +
			    "            font-weight: 600;" +
			    "        }" +
			    "        /* Content Styles */" +
			    "        .email-content {" +
			    "            padding: 20px;" +
			    "            font-size: 16px;" +
			    "            color: #555555;" +
			    "            line-height: 1.6;" +
			    "        }" +
			    "        .email-content p {" +
			    "            margin: 0 0 10px;" +
			    "        }" +
			    "        .details-table td {" +
			    "            padding: 12px;" +
			    "            background-color: #ECF0F1;" +
			    "            border: 1px solid #BDC3C7;" +
			    "            text-align: left;" +
			    "        }" +
			    "        .details-table td strong {" +
			    "            color: #E74C3C;" +
			    "        }" +
			    "        /* Footer styles */" +
			    "        .email-footer {" +
			    "            background-color: #E74C3C;" +
			    "            color: #BDC3C7;" +
			    "            text-align: center;" +
			    "            padding: 20px;" +
			    "            font-size: 12px;" +
			    "            border-bottom-left-radius: 8px;" +
			    "            border-bottom-right-radius: 8px;" +
			    "        }" +
			    "        /* Button hover effect */" +
			    "        .btn {" +
			    "            background-color: #E74C3C;" +
			    "            color: white;" +
			    "            padding: 10px 20px;" +
			    "            text-align: center;" +
			    "            border-radius: 4px;" +
			    "            text-decoration: none;" +
			    "            display: inline-block;" +
			    "            transition: background-color 0.3s ease;" +
			    "        }" +
			    "        .btn:hover {" +
			    "            background-color: #C0392B;" +
			    "        }" +
			    "        /* Fade-in animation */" +
			    "        @keyframes fadeIn {" +
			    "            0% { opacity: 0; }" +
			    "            100% { opacity: 1; }" +
			    "        }" +
			    "    </style>" +
			    "</head>" +
			    "<body>" +
			    "    <table>" +
			    "        <tr>" +
			    "            <td align=\"center\">" +
			    "                <div class=\"email-container\">" +
			    "                    <div class=\"email-header\">" +
			    "                        <h2>Leave Rejection Notification</h2>" +
			    "                    </div>" +
			    "                    <div class=\"email-content\">" +
			    "                        <p>Dear <strong>" + contents.getFirstName() + " " + contents.getLastName() + "</strong>,</p>" +
			    "                        <p>We regret to inform you that your leave request has been <strong>rejected</strong>.</p>" +
			    "                        <p>Below are the details of your request:</p>" +
			    "                        <table class=\"details-table\" width=\"100%\" cellpadding=\"5\" cellspacing=\"0\">" +
			    "                            <tr>" +
			    "                                <td><strong>Leave Type:</strong></td>" +
			    "                                <td>" + contents.getLeaveType() + "</td>" +
			    "                            </tr>" +
			    "                            <tr>" +
			    "                                <td><strong>Start Date:</strong></td>" +
			    "                                <td>" + contents.getStartDate() + "</td>" +
			    "                            </tr>" +
			    "                            <tr>" +
			    "                                <td><strong>End Date:</strong></td>" +
			    "                                <td>" + contents.getEndDate() + "</td>" +
			    "                            </tr>" +
			    "                        </table>" +
			    "                        <p>If you have any questions or concerns, feel free to contact HR.</p>" +
			    "                        <p>Regards,</p>" +
			    "                        <p>HR Team</p>" +
			    "                        <a href=\"https://www.linkedin.com/in/anil-kumar-a525337\" class=\"btn\">Contact HR</a>" +
			    "                    </div>" +
			    "                    <div class=\"email-footer\">" +
			    "                        <p>&copy; 2025 MultiTenantHRManagement. All Rights Reserved.</p>" +
			    "                    </div>" +
			    "                </div>" +
			    "            </td>" +
			    "        </tr>" +
			    "    </table>" +
			    "</body>" +
			    "</html>";

		return mailTemplate;

	}
	
	
	

	@Transactional
	 public MailDto approveOrRejectLeaveRequest(String leaveUuid) {
	     // Call the repository to fetch the leave request
	     Optional<LeaveManagement> leaveRequest = leaveRepoDaoImpl.findLeaveByUuidForUserName(leaveUuid);
	     

	     if (!leaveRequest.isPresent()) {
	         throw new LeaveRequestNotFoundException(HrManagementEnum.LEAVE_REQUEST_NOT_FOUND);
	     }

	     LeaveManagement leave = leaveRequest.get();
	     List<Employee> wrkDetails = employeeRepoDaoImpl.getUsersById(leave.getEmployeeUuid());
	     
	     Employee employee = wrkDetails.get(0);
	     List<Employee> tenantEmployeeDetails = leaveRepoDaoImpl.getHrDetails(employee.getTenantUuid());
	     
	     List<Roles> getAllRoles = employeeRepoDaoImpl.getAllAvailableRoles();
	     
	     String hrMail = tenantEmployeeDetails.stream()
	    		    .filter(f -> f.getRoleUuid()
	    		        .equals(getAllRoles.stream()
	    		            .filter(filter -> filter.getRoleName().equals("HR_MANAGER"))
	    		            .map(Roles::getRoleUuid)
	    		            .findFirst()
	    		            .orElseThrow(() -> new EmployeeIdNullException(HrManagementEnum.EMPLOYEE_VALID_NOT_FOUND))
	    		        )
	    		    )
	    		    .map(Employee::getEmail)
	    		    .findFirst()
	    		    .orElseThrow(() -> new EmployeeIdNullException(HrManagementEnum.EMPLOYEE_VALID_NOT_FOUND));

	     
	     // Get the employee name and other leave details
	                         // Assuming EmployeeId refers to a user
	     String leaveType = leave.getReason(); // Modify this to fetch the actual leave type if needed
	     String startDate = leave.getStartDate().toString();
	     String endDate = leave.getEndDate().toString();
	     String userName = employee.getUserName();
	     String firstName = employee.getFirstName();
	     String lastName = employee.getLastName();
	     String email = employee.getEmail();
	     String employeeEmail = employee.getEmail();
	     

	     // Create and populate the MailDto
	     MailDto mailDto = new MailDto(); // You should ideally fetch the actual employee's name from Employee table
	     mailDto.setLeaveType(leaveType);
	     mailDto.setStartDate(startDate);
	     mailDto.setEndDate(endDate);
	     mailDto.setUserName(userName);
	     mailDto.setEmail(hrMail);
	     mailDto.setFirstName(firstName);
	     mailDto.setLastName(lastName);
	     mailDto.setEmployeeEmail(employeeEmail);

         System.out.println(userName);
         System.out.println(email);
	     System.out.println(leaveType);   
	     System.out.println(startDate);
	     System.out.println(endDate);
	    

	     return mailDto  ;
	 }

}
