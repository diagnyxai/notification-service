package com.diagnyx.notification.entity;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "email_queue")
public class EmailQueue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(name = "recipient_email", nullable = false)
    private String recipientEmail;
    
    @Column(nullable = false)
    private String subject;
    
    @Column(name = "body_html", nullable = false, columnDefinition = "TEXT")
    private String bodyHtml;
    
    @Column(name = "body_text", columnDefinition = "TEXT")
    private String bodyText;
    
    @Column(name = "template_id", nullable = false)
    private String templateId;
    
    @Column(nullable = false)
    private String status = "pending";
    
    @Column
    private Integer priority = 3;
    
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    @Column(name = "max_retries")
    private Integer maxRetries = 3;
    
    @Column(name = "scheduled_at")
    private OffsetDateTime scheduledAt;
    
    @Column(name = "sent_at")
    private OffsetDateTime sentAt;
    
    @Column(name = "delivered_at")
    private OffsetDateTime deliveredAt;
    
    @Column(name = "error_message")
    private String errorMessage;
    
    @Column(name = "alert_trigger_id")
    private UUID alertTriggerId;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
    
    // Default constructor
    public EmailQueue() {
    }
    
    // Getters and setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getRecipientEmail() {
        return recipientEmail;
    }
    
    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
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
    
    public String getTemplateId() {
        return templateId;
    }
    
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
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
    
    public OffsetDateTime getScheduledAt() {
        return scheduledAt;
    }
    
    public void setScheduledAt(OffsetDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
    
    public OffsetDateTime getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(OffsetDateTime sentAt) {
        this.sentAt = sentAt;
    }
    
    public OffsetDateTime getDeliveredAt() {
        return deliveredAt;
    }
    
    public void setDeliveredAt(OffsetDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public UUID getAlertTriggerId() {
        return alertTriggerId;
    }
    
    public void setAlertTriggerId(UUID alertTriggerId) {
        this.alertTriggerId = alertTriggerId;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Builder pattern implementation
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private EmailQueue entity = new EmailQueue();
        
        public Builder id(UUID id) {
            entity.id = id;
            return this;
        }
        
        public Builder recipientEmail(String recipientEmail) {
            entity.recipientEmail = recipientEmail;
            return this;
        }
        
        public Builder subject(String subject) {
            entity.subject = subject;
            return this;
        }
        
        public Builder bodyHtml(String bodyHtml) {
            entity.bodyHtml = bodyHtml;
            return this;
        }
        
        public Builder bodyText(String bodyText) {
            entity.bodyText = bodyText;
            return this;
        }
        
        public Builder templateId(String templateId) {
            entity.templateId = templateId;
            return this;
        }
        
        public Builder status(String status) {
            entity.status = status;
            return this;
        }
        
        public Builder priority(Integer priority) {
            entity.priority = priority;
            return this;
        }
        
        public Builder retryCount(Integer retryCount) {
            entity.retryCount = retryCount;
            return this;
        }
        
        public Builder maxRetries(Integer maxRetries) {
            entity.maxRetries = maxRetries;
            return this;
        }
        
        public Builder scheduledAt(OffsetDateTime scheduledAt) {
            entity.scheduledAt = scheduledAt;
            return this;
        }
        
        public Builder sentAt(OffsetDateTime sentAt) {
            entity.sentAt = sentAt;
            return this;
        }
        
        public Builder deliveredAt(OffsetDateTime deliveredAt) {
            entity.deliveredAt = deliveredAt;
            return this;
        }
        
        public Builder errorMessage(String errorMessage) {
            entity.errorMessage = errorMessage;
            return this;
        }
        
        public Builder alertTriggerId(UUID alertTriggerId) {
            entity.alertTriggerId = alertTriggerId;
            return this;
        }
        
        public Builder metadata(Map<String, Object> metadata) {
            entity.metadata = metadata;
            return this;
        }
        
        public EmailQueue build() {
            return entity;
        }
    }
} 