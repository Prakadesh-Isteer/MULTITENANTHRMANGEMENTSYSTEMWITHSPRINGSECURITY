package com.isteer.mail.service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.isteer.dto.MailDto;
import com.isteer.enums.HrManagementEnum;
import com.isteer.exception.MailTriggerException;

import jakarta.mail.internet.MimeMessage;

@Component
public class MailSenderService {
	
	@Autowired
	MailTemplateService mailTemplateService;
	
	@Autowired
	JavaMailSender javaMailSender;
    private static final Logger logger = LogManager.getLogger(MailSenderService.class);

    
	 public void sendLeaveApprovalEmail(MailDto contents) {
		    try {
		        // Prepare the email content using the MailDto
		        String emailContent = mailTemplateService.buildLeaveApprovalEmail(contents);

		        // Send email using JavaMailSender (make sure JavaMailSender is properly configured)
		        MimeMessage message = javaMailSender.createMimeMessage();
		        MimeMessageHelper helperMesaage=  new MimeMessageHelper(message, true);
		        helperMesaage.setTo(contents.getEmployeeEmail());
		        helperMesaage.setCc(contents.getEmail());         // Assuming the username corresponds to the email address
		        helperMesaage.setSubject("Leave Approval Notification");
		        helperMesaage.setText(emailContent, true);
		        javaMailSender.send(message);  // Send the email
		        logger.info("{} email sent to: {}",  contents.getEmployeeEmail());
		    } catch (Exception e) {
	            logger.error("Failed to send {} email to {}: {}", contents.getEmployeeEmail());

		      throw new MailTriggerException(HrManagementEnum.Mail_not_send);
		    }
		}
	 
	 
	 public void sendLeaveRejectEmail(MailDto contents) {
		    try {
		        // Prepare the email content using the MailDto
		        String emailContent = mailTemplateService.buildLeaveRejectEmail(contents);

		        // Send email using JavaMailSender (make sure JavaMailSender is properly configured)
		        MimeMessage message = javaMailSender.createMimeMessage();
		        MimeMessageHelper helperMesaage=  new MimeMessageHelper(message, true);
		        helperMesaage.setTo(contents.getEmployeeEmail());
		        helperMesaage.setCc(contents.getEmail());         // Assuming the username corresponds to the email address
		        helperMesaage.setSubject("Leave Rejection Notification");
		        helperMesaage.setText(emailContent, true);
		        javaMailSender.send(message); 
		        logger.info("{} email sent to: {}",  contents.getEmployeeEmail());
// Send the email
		    } catch (Exception e) {
	            logger.error("Failed to send {} email to {}: {}", contents.getEmployeeEmail());

			      throw new MailTriggerException(HrManagementEnum.Mail_reject_notsend);

		        // Handle the email sending failure
		    }
		}




}
