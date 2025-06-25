package com.diagnyx.notification.dto;

import java.util.UUID;
import java.time.ZonedDateTime;

/**
 * DTO for email queue items
 */
public class EmailQueueDto {
    
    private UUID id;
    private UUID alertTriggerId;
    private String recipientEmail;
    private String templateId;
    private String subject;
    private String bodyHtml;
    private String bodyText;
    private String status; // pending, sent, delivered, bounced, failed
    private Integer priority;
    private ZonedDateTime scheduledAt;
    private Integer retryCount;
    private Integer maxRetries;
    
    public EmailQueueDto() {
    }
    
    public EmailQueueDto(UUID id, UUID alertTriggerId, String recipientEmail, String templateId,
                        String subject, String bodyHtml, String bodyText, String status,
                        Integer priority, ZonedDateTime scheduledAt, Integer retryCount, Integer maxRetries) {
        this.id = id;
        this.alertTriggerId = alertTriggerId;
        this.recipientEmail = recipientEmail;
        this.templateId = templateId;
        this.subject = subject;
        this.bodyHtml = bodyHtml;
        this.bodyText = bodyText;
        this.status = status;
        this.priority = priority;
        this.scheduledAt = scheduledAt;
        this.retryCount = retryCount;
        this.maxRetries = maxRetries;
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getAlertTriggerId() {
        return alertTriggerId;
    }
    
    public void setAlertTriggerId(UUID alertTriggerId) {
        this.alertTriggerId = alertTriggerId;
    }
    
    public String getRecipientEmail() {
        return recipientEmail;
    }
    
    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }
    
    public String getTemplateId() {
        return templateId;
    }
    
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getBodyHtml() {
        return bodyHtml;
    }
    
    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }
    
    public String getBodyText() {
        return bodyText;
    }
    
    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getPriority() {
        return priority;
    }
    
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
    public ZonedDateTime getScheduledAt() {
        return scheduledAt;
    }
    
    public void setScheduledAt(ZonedDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
    
    public Integer getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
    
    public Integer getMaxRetries() {
        return maxRetries;
    }
    
    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }
} 