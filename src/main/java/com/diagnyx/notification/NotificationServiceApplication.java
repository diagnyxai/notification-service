package com.diagnyx.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Diagnyx Notification Service Application
 * 
 * Multi-channel notification service providing:
 * - Email notifications with template support
 * - SMS notifications via Twilio
 * - Push notifications via Firebase FCM
 * - Webhook notifications
 * - Slack integrations
 * - Delivery tracking and analytics
 * - Retry logic with exponential backoff
 * - Rate limiting and throttling
 * - Template management system
 * - User preference management
 * - Notification scheduling
 * - Bounce and complaint handling
 * 
 * Features:
 * - Multi-provider support (SendGrid, Mailgun, SES)
 * - Template engines (Thymeleaf, FreeMarker, Handlebars)
 * - Delivery status tracking
 * - Notification batching
 * - A/B testing for templates
 * - Internationalization support
 * - GDPR compliance features
 * - Real-time delivery analytics
 * 
 * @author Diagnyx Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableConfigurationProperties
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
public class NotificationServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Diagnyx Notification Service...");
        
        try {
            SpringApplication app = new SpringApplication(NotificationServiceApplication.class);
            
            // Set default profiles if none specified
            if (System.getProperty("spring.profiles.active") == null) {
                app.setAdditionalProfiles("development");
            }
            
            app.run(args);
            logger.info("Diagnyx Notification Service started successfully on port 8081");
        } catch (Exception e) {
            logger.error("Failed to start Diagnyx Notification Service", e);
            System.exit(1);
        }
    }
} 