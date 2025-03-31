package com.isteer.mail.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.isteer.dto.MailDto;

import jakarta.mail.internet.MimeMessage;

@Component
public class MailSenderService {
	
	@Autowired
	MailTemplateService mailTemplateService;
	
	@Autowired
	JavaMailSender javaMailSender;

    
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
		    } catch (Exception e) {
		        e.printStackTrace();
		        // Handle the email sending failure
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
		        javaMailSender.send(message);  // Send the email
		    } catch (Exception e) {
		        e.printStackTrace();
		        // Handle the email sending failure
		    }
		}




}
