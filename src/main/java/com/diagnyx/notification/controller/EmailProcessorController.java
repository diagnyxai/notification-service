package com.diagnyx.notification.controller;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.diagnyx.notification.dto.EmailQueueItemDTO;
import com.diagnyx.notification.service.EmailProcessorService;

import jakarta.validation.Valid;

/**
 * Controller for email processing operations
 * Replaces the Supabase edge function for email processing
 */
@RestController
@RequestMapping("/api/v1/email-processor")
public class EmailProcessorController {
    
    private static final Logger log = LoggerFactory.getLogger(EmailProcessorController.class);
    
    private final EmailProcessorService emailProcessorService;
    
    @Autowired
    public EmailProcessorController(EmailProcessorService emailProcessorService) {
        this.emailProcessorService = emailProcessorService;
    }
    
    /**
     * Process the email queue manually
     * This endpoint can be called by a scheduler or manually
     */
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processEmailQueue() {
        log.info("Manual request to process email queue");
        
        try {
            emailProcessorService.processEmailQueue();
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Email queue processing initiated"
            ));
        } catch (Exception e) {
            log.error("Error processing email queue: {}", e.getMessage(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", "Failed to process email queue",
                    "error", e.getMessage()
                ));
        }
    }
    
    /**
     * Queue an email for delivery
     */
    @PostMapping("/queue")
    public ResponseEntity<EmailQueueItemDTO> queueEmail(@Valid @RequestBody EmailQueueItemDTO emailDto) {
        log.info("Request to queue email to {}", emailDto.getRecipientEmail());
        
        try {
            EmailQueueItemDTO queuedEmail = emailProcessorService.queueEmail(emailDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(queuedEmail);
        } catch (Exception e) {
            log.error("Error queueing email: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Queue an alert notification email
     */
    @PostMapping("/queue/alert")
    public ResponseEntity<EmailQueueItemDTO> queueAlertEmail(
            @RequestParam UUID alertTriggerId,
            @RequestParam String recipientEmail,
            @RequestParam(defaultValue = "alert-notification") String templateType) {
        
        log.info("Request to queue alert email for trigger {} to {}", alertTriggerId, recipientEmail);
        
        try {
            EmailQueueItemDTO queuedEmail = emailProcessorService.queueAlertEmail(
                alertTriggerId, recipientEmail, templateType);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(queuedEmail);
        } catch (Exception e) {
            log.error("Error queueing alert email: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Get email queue statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getQueueStatistics() {
        log.info("Request for email queue statistics");
        
        try {
            Map<String, Long> stats = emailProcessorService.getQueueStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting queue statistics: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Handle webhook from email providers
     * This endpoint would receive delivery status updates from email providers
     */
    @PostMapping("/webhook")
    public ResponseEntity<Map<String, String>> handleWebhook(@RequestBody Map<String, Object> payload) {
        log.info("Received webhook from email provider");
        
        // In a real implementation, this would process delivery status updates
        // from the email provider (e.g., SendGrid, Mailgun, AWS SES)
        log.debug("Webhook payload: {}", payload);
        
        return ResponseEntity.ok(Map.of(
            "status", "received",
            "message", "Webhook processed successfully"
        ));
    }
} 