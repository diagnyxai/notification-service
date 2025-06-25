package com.diagnyx.notification.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diagnyx.notification.dto.EmailQueueItemDTO;
import com.diagnyx.notification.entity.EmailQueue;
import com.diagnyx.notification.repository.EmailQueueRepository;

@Service
public class EmailProcessorService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailProcessorService.class);
    
    private final EmailQueueRepository emailQueueRepository;
    private final EmailSenderService emailSenderService;
    
    @Value("${email.processor.batch-size:10}")
    private int batchSize;
    
    @Autowired
    public EmailProcessorService(EmailQueueRepository emailQueueRepository, EmailSenderService emailSenderService) {
        this.emailQueueRepository = emailQueueRepository;
        this.emailSenderService = emailSenderService;
    }
    
    /**
     * Process the email queue at regular intervals
     * This replaces the Supabase edge function that was triggered periodically
     */
    @Scheduled(fixedDelayString = "${email.processor.interval:60000}")
    @Transactional
    public void processEmailQueue() {
        logger.info("Starting email queue processing...");
        
        try {
            // Get next batch of emails to process
            List<EmailQueue> pendingEmails = emailQueueRepository.findNextBatchToProcess(
                OffsetDateTime.now(), 
                PageRequest.of(0, batchSize)
            );
            
            if (pendingEmails.isEmpty()) {
                logger.info("No pending emails to process");
                return;
            }
            
            logger.info("Processing {} emails", pendingEmails.size());
            
            // Process each email
            for (EmailQueue email : pendingEmails) {
                try {
                    processEmail(email);
                } catch (Exception e) {
                    logger.error("Error processing email {}: {}", email.getId(), e.getMessage(), e);
                    emailQueueRepository.markAsFailed(email.getId(), "failed", e.getMessage());
                }
            }
            
        } catch (Exception e) {
            logger.error("Error in email queue processing: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Process a single email
     */
    @Transactional
    public void processEmail(EmailQueue email) throws Exception {
        logger.info("Processing email {} to {}", email.getId(), email.getRecipientEmail());
        
        try {
            // Send the email
            String messageId = emailSenderService.sendEmail(
                email.getRecipientEmail(),
                email.getSubject(),
                email.getBodyHtml(),
                email.getBodyText()
            );
            
            // Mark as sent
            emailQueueRepository.markAsSent(email.getId(), "sent", OffsetDateTime.now());
            
            logger.info("Email {} sent successfully, message ID: {}", email.getId(), messageId);
            
        } catch (Exception e) {
            logger.error("Failed to send email {}: {}", email.getId(), e.getMessage());
            
            // Determine if we should retry
            if (email.getRetryCount() < email.getMaxRetries()) {
                emailQueueRepository.markAsFailed(email.getId(), "retry", e.getMessage());
                logger.info("Email {} scheduled for retry ({}/{})", 
                    email.getId(), email.getRetryCount() + 1, email.getMaxRetries());
            } else {
                emailQueueRepository.markAsFailed(email.getId(), "failed", e.getMessage());
                logger.warn("Email {} failed permanently after {} attempts", 
                    email.getId(), email.getRetryCount() + 1);
            }
            
            throw e;
        }
    }
    
    /**
     * Queue an email for delivery
     */
    @Transactional
    public EmailQueueItemDTO queueEmail(EmailQueueItemDTO emailDto) {
        logger.info("Queueing email to {}", emailDto.getRecipientEmail());
        
        EmailQueue email = EmailQueue.builder()
            .recipientEmail(emailDto.getRecipientEmail())
            .subject(emailDto.getSubject())
            .bodyHtml(emailDto.getBodyHtml())
            .bodyText(emailDto.getBodyText())
            .templateId(emailDto.getTemplateId())
            .status("pending")
            .priority(emailDto.getPriority() != null ? emailDto.getPriority() : 3)
            .retryCount(0)
            .maxRetries(emailDto.getMaxRetries() != null ? emailDto.getMaxRetries() : 3)
            .scheduledAt(emailDto.getScheduledAt() != null ? emailDto.getScheduledAt() : OffsetDateTime.now())
            .alertTriggerId(emailDto.getAlertTriggerId())
            .metadata(emailDto.getMetadata())
            .build();
        
        EmailQueue savedEmail = emailQueueRepository.save(email);
        
        logger.info("Email queued with ID: {}", savedEmail.getId());
        
        return mapToDto(savedEmail);
    }
    
    /**
     * Queue an alert notification email
     */
    @Transactional
    public EmailQueueItemDTO queueAlertEmail(UUID alertTriggerId, String recipientEmail, String templateId) {
        logger.info("Queueing alert email for trigger {} to {}", alertTriggerId, recipientEmail);
        
        // In a real implementation, we would fetch alert details and populate template variables
        Map<String, Object> metadata = Map.of(
            "alertTriggerId", alertTriggerId.toString(),
            "templateType", templateId
        );
        
        EmailQueueItemDTO emailDto = EmailQueueItemDTO.builder()
            .recipientEmail(recipientEmail)
            .subject("Alert Notification")  // Would be populated from template in real implementation
            .bodyHtml("<p>Alert notification</p>")  // Would be populated from template in real implementation
            .bodyText("Alert notification")  // Would be populated from template in real implementation
            .templateId(templateId)
            .priority(1)  // High priority for alerts
            .alertTriggerId(alertTriggerId)
            .metadata(metadata)
            .build();
        
        return queueEmail(emailDto);
    }
    
    /**
     * Get email queue statistics
     */
    public Map<String, Long> getQueueStatistics() {
        return Map.of(
            "pending", emailQueueRepository.countByStatus("pending"),
            "sent", emailQueueRepository.countByStatus("sent"),
            "delivered", emailQueueRepository.countByStatus("delivered"),
            "failed", emailQueueRepository.countByStatus("failed"),
            "retry", emailQueueRepository.countByStatus("retry")
        );
    }
    
    /**
     * Map entity to DTO
     */
    private EmailQueueItemDTO mapToDto(EmailQueue email) {
        return EmailQueueItemDTO.builder()
            .id(email.getId())
            .recipientEmail(email.getRecipientEmail())
            .subject(email.getSubject())
            .bodyHtml(email.getBodyHtml())
            .bodyText(email.getBodyText())
            .templateId(email.getTemplateId())
            .status(email.getStatus())
            .priority(email.getPriority())
            .retryCount(email.getRetryCount())
            .maxRetries(email.getMaxRetries())
            .scheduledAt(email.getScheduledAt())
            .sentAt(email.getSentAt())
            .deliveredAt(email.getDeliveredAt())
            .errorMessage(email.getErrorMessage())
            .alertTriggerId(email.getAlertTriggerId())
            .metadata(email.getMetadata())
            .createdAt(email.getCreatedAt())
            .updatedAt(email.getUpdatedAt())
            .build();
    }
} 