package com.diagnyx.notification.dto;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmailQueueItemDTO {
    private UUID id;
    private String recipientEmail;
    private String subject;
    private String bodyHtml;
    private String bodyText;
    private String templateId;
    private String status;
    private Integer priority;
    private Integer retryCount;
    private Integer maxRetries;
    private OffsetDateTime scheduledAt;
    private OffsetDateTime sentAt;
    private OffsetDateTime deliveredAt;
    private String errorMessage;
    private UUID alertTriggerId;
    private Map<String, Object> metadata;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    
    // Default constructor
    public EmailQueueItemDTO() {
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
        private EmailQueueItemDTO dto = new EmailQueueItemDTO();
        
        public Builder id(UUID id) {
            dto.id = id;
            return this;
        }
        
        public Builder recipientEmail(String recipientEmail) {
            dto.recipientEmail = recipientEmail;
            return this;
        }
        
        public Builder subject(String subject) {
            dto.subject = subject;
            return this;
        }
        
        public Builder bodyHtml(String bodyHtml) {
            dto.bodyHtml = bodyHtml;
            return this;
        }
        
        public Builder bodyText(String bodyText) {
            dto.bodyText = bodyText;
            return this;
        }
        
        public Builder templateId(String templateId) {
            dto.templateId = templateId;
            return this;
        }
        
        public Builder status(String status) {
            dto.status = status;
            return this;
        }
        
        public Builder priority(Integer priority) {
            dto.priority = priority;
            return this;
        }
        
        public Builder retryCount(Integer retryCount) {
            dto.retryCount = retryCount;
            return this;
        }
        
        public Builder maxRetries(Integer maxRetries) {
            dto.maxRetries = maxRetries;
            return this;
        }
        
        public Builder scheduledAt(OffsetDateTime scheduledAt) {
            dto.scheduledAt = scheduledAt;
            return this;
        }
        
        public Builder sentAt(OffsetDateTime sentAt) {
            dto.sentAt = sentAt;
            return this;
        }
        
        public Builder deliveredAt(OffsetDateTime deliveredAt) {
            dto.deliveredAt = deliveredAt;
            return this;
        }
        
        public Builder errorMessage(String errorMessage) {
            dto.errorMessage = errorMessage;
            return this;
        }
        
        public Builder alertTriggerId(UUID alertTriggerId) {
            dto.alertTriggerId = alertTriggerId;
            return this;
        }
        
        public Builder metadata(Map<String, Object> metadata) {
            dto.metadata = metadata;
            return this;
        }
        
        public Builder createdAt(OffsetDateTime createdAt) {
            dto.createdAt = createdAt;
            return this;
        }
        
        public Builder updatedAt(OffsetDateTime updatedAt) {
            dto.updatedAt = updatedAt;
            return this;
        }
        
        public EmailQueueItemDTO build() {
            return dto;
        }
    }
} 