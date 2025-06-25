package com.diagnyx.notification.service;

/**
 * Service interface for sending emails
 */
public interface EmailSenderService {
    
    /**
     * Send an email
     * 
     * @param to recipient email address
     * @param subject email subject
     * @param htmlContent HTML content of the email
     * @param textContent Plain text content of the email (optional)
     * @return message ID from the email provider
     * @throws Exception if sending fails
     */
    String sendEmail(String to, String subject, String htmlContent, String textContent) throws Exception;
} 