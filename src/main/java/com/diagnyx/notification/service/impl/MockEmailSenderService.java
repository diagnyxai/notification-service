package com.diagnyx.notification.service.impl;

import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.diagnyx.notification.service.EmailSenderService;

/**
 * Mock implementation of EmailSenderService for development/testing
 * In production, this would be replaced with a real email provider like SendGrid, AWS SES, etc.
 */
@Service
public class MockEmailSenderService implements EmailSenderService {
    
    private static final Logger logger = LoggerFactory.getLogger(MockEmailSenderService.class);
    private static final Random random = new Random();
    
    @Override
    public String sendEmail(String to, String subject, String htmlContent, String textContent) throws Exception {
        logger.info("MOCK: Sending email to {} with subject: {}", to, subject);
        logger.debug("MOCK: HTML content: {}", htmlContent.substring(0, Math.min(100, htmlContent.length())) + "...");
        
        // Simulate occasional failures (10% chance)
        if (random.nextDouble() < 0.1) {
            logger.warn("MOCK: Simulating email sending failure");
            throw new Exception("Mock email provider error: Simulated failure");
        }
        
        // Simulate processing delay
        try {
            Thread.sleep(50 + random.nextInt(200));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String messageId = "mock_" + UUID.randomUUID().toString();
        logger.info("MOCK: Email sent successfully with ID: {}", messageId);
        
        return messageId;
    }
} 