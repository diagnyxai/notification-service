package com.diagnyx.notification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.diagnyx.notification.service.EmailService;
import com.diagnyx.notification.dto.EmailPreferencesDto;
import com.diagnyx.notification.dto.EmailLogDto;
import com.diagnyx.notification.dto.EmailDeliveryRequest;
import com.diagnyx.notification.dto.TestEmailRequest;
import com.diagnyx.notification.dto.EmailTemplateDto;
import com.diagnyx.notification.dto.EmailQueueDto;
import com.diagnyx.notification.dto.EmailDeliveryStatsDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for email notification operations
 */
@RestController
@RequestMapping("/api/notifications/email")
public class EmailController {

    @Autowired
    private EmailService emailService;
    
    /**
     * Get email preferences for the current user
     */
    @GetMapping("/preferences")
    public ResponseEntity<EmailPreferencesDto> getEmailPreferences(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(emailService.getEmailPreferences(userId));
    }
    
    /**
     * Update email preferences
     */
    @PutMapping("/preferences")
    public ResponseEntity<EmailPreferencesDto> updateEmailPreferences(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody EmailPreferencesDto request) {
        return ResponseEntity.ok(emailService.updateEmailPreferences(userId, request));
    }
    
    /**
     * Partial update of email preferences
     */
    @PatchMapping("/preferences")
    public ResponseEntity<EmailPreferencesDto> patchEmailPreferences(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(emailService.patchEmailPreferences(userId, updates));
    }
    
    /**
     * Send a test email
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> sendTestEmail(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody TestEmailRequest request) {
        boolean success = emailService.sendTestEmail(userId, request);
        Map<String, Object> response = Map.of(
            "success", success,
            "message", success ? "Test email sent successfully" : "Failed to send test email"
        );
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get email delivery logs
     */
    @GetMapping("/logs")
    public ResponseEntity<Page<EmailLogDto>> getEmailLogs(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) String alertId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(emailService.getEmailLogs(userId, alertId, PageRequest.of(page, size)));
    }
    
    /**
     * Log email delivery status
     */
    @PostMapping("/log")
    public ResponseEntity<Void> logEmailDelivery(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody EmailDeliveryRequest request) {
        emailService.logEmailDelivery(userId, request);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get all active email templates
     */
    @GetMapping("/templates")
    public ResponseEntity<List<EmailTemplateDto>> getEmailTemplates() {
        return ResponseEntity.ok(emailService.getEmailTemplates());
    }
    
    /**
     * Get a specific email template by type
     */
    @GetMapping("/templates/{templateType}")
    public ResponseEntity<EmailTemplateDto> getEmailTemplateByType(
            @PathVariable String templateType) {
        return ResponseEntity.ok(emailService.getEmailTemplateByType(templateType));
    }
    
    /**
     * Queue an alert email
     */
    @PostMapping("/queue/alert")
    public ResponseEntity<Map<String, Object>> queueAlertEmail(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam String alertTriggerId,
            @RequestParam String recipientEmail,
            @RequestParam(defaultValue = "alert_triggered") String templateType) {
        UUID emailId = emailService.queueAlertEmail(userId, alertTriggerId, recipientEmail, templateType);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "emailId", emailId.toString()
        ));
    }
    
    /**
     * Get email queue items
     */
    @GetMapping("/queue")
    public ResponseEntity<List<EmailQueueDto>> getEmailQueue(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(emailService.getEmailQueue(userId, status));
    }
    
    /**
     * Get pending email queue items
     */
    @GetMapping("/queue/pending")
    public ResponseEntity<List<EmailQueueDto>> getPendingEmails(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(emailService.getEmailQueue(userId, "pending"));
    }
    
    /**
     * Get email delivery statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<EmailDeliveryStatsDto> getEmailDeliveryStats(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(emailService.getEmailDeliveryStats(userId));
    }
    
    /**
     * Track email event (opened, clicked, etc.)
     */
    @PostMapping("/track/{emailQueueId}")
    public ResponseEntity<Void> trackEmailEvent(
            @PathVariable String emailQueueId,
            @RequestParam String eventType,
            @RequestBody Map<String, Object> metadata) {
        emailService.trackEmailEvent(emailQueueId, eventType, metadata);
        return ResponseEntity.ok().build();
    }
} 