package com.diagnyx.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.diagnyx.notification.dto.EmailPreferencesDto;
import com.diagnyx.notification.dto.EmailLogDto;
import com.diagnyx.notification.dto.EmailDeliveryRequest;
import com.diagnyx.notification.dto.TestEmailRequest;
import com.diagnyx.notification.dto.EmailTemplateDto;
import com.diagnyx.notification.dto.EmailQueueDto;
import com.diagnyx.notification.dto.EmailDeliveryStatsDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for email operations
 */
@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Get email preferences for a user
     */
    public EmailPreferencesDto getEmailPreferences(String userId) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT id, email, format, frequency, is_active, is_verified, " +
                "quiet_hours_enabled, quiet_hours_start, quiet_hours_end, " +
                "severity_filter, timezone " +
                "FROM email_preferences WHERE user_id = ?",
                (rs, rowNum) -> mapToEmailPreferencesDto(rs),
                UUID.fromString(userId)
            );
        } catch (Exception e) {
            logger.error("Error retrieving email preferences for user {}: {}", userId, e.getMessage());
            // Return default preferences if none found
            EmailPreferencesDto defaults = new EmailPreferencesDto();
            defaults.setFormat("html");
            defaults.setFrequency("immediate");
            defaults.setActive(true);
            defaults.setVerified(false);
            defaults.setQuietHoursEnabled(false);
            defaults.setQuietHoursStart(LocalTime.of(22, 0));
            defaults.setQuietHoursEnd(LocalTime.of(8, 0));
            defaults.setSeverityFilter("all");
            defaults.setTimezone("UTC");
            return defaults;
        }
    }
    
    /**
     * Update email preferences
     */
    public EmailPreferencesDto updateEmailPreferences(String userId, EmailPreferencesDto dto) {
        try {
            // Check if preferences exist
            boolean exists = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM email_preferences WHERE user_id = ?",
                Integer.class,
                UUID.fromString(userId)
            ) > 0;
            
            if (exists) {
                // Update existing preferences
                jdbcTemplate.update(
                    "UPDATE email_preferences SET " +
                    "email = ?, format = ?, frequency = ?, is_active = ?, " +
                    "quiet_hours_enabled = ?, quiet_hours_start = ?, quiet_hours_end = ?, " +
                    "severity_filter = ?, timezone = ?, updated_at = now() " +
                    "WHERE user_id = ?",
                    dto.getEmail(),
                    dto.getFormat(),
                    dto.getFrequency(),
                    dto.isActive(),
                    dto.isQuietHoursEnabled(),
                    dto.getQuietHoursStart(),
                    dto.getQuietHoursEnd(),
                    dto.getSeverityFilter(),
                    dto.getTimezone(),
                    UUID.fromString(userId)
                );
            } else {
                // Insert new preferences
                UUID id = UUID.randomUUID();
                jdbcTemplate.update(
                    "INSERT INTO email_preferences " +
                    "(id, user_id, email, format, frequency, is_active, is_verified, " +
                    "quiet_hours_enabled, quiet_hours_start, quiet_hours_end, " +
                    "severity_filter, timezone, unsubscribe_token) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    id,
                    UUID.fromString(userId),
                    dto.getEmail(),
                    dto.getFormat(),
                    dto.getFrequency(),
                    dto.isActive(),
                    dto.isVerified(),
                    dto.isQuietHoursEnabled(),
                    dto.getQuietHoursStart(),
                    dto.getQuietHoursEnd(),
                    dto.getSeverityFilter(),
                    dto.getTimezone(),
                    UUID.randomUUID() // Generate unsubscribe token
                );
                dto.setId(id);
            }
            
            return dto;
        } catch (Exception e) {
            logger.error("Error updating email preferences for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to update email preferences", e);
        }
    }
    
    /**
     * Partial update of email preferences
     */
    public EmailPreferencesDto patchEmailPreferences(String userId, Map<String, Object> updates) {
        // Get current preferences
        EmailPreferencesDto currentPrefs = getEmailPreferences(userId);
        
        // Apply updates
        if (updates.containsKey("format")) {
            currentPrefs.setFormat((String) updates.get("format"));
        }
        if (updates.containsKey("frequency")) {
            currentPrefs.setFrequency((String) updates.get("frequency"));
        }
        if (updates.containsKey("isActive")) {
            currentPrefs.setActive((Boolean) updates.get("isActive"));
        }
        if (updates.containsKey("quietHoursEnabled")) {
            currentPrefs.setQuietHoursEnabled((Boolean) updates.get("quietHoursEnabled"));
        }
        if (updates.containsKey("severityFilter")) {
            currentPrefs.setSeverityFilter((String) updates.get("severityFilter"));
        }
        
        // Save updated preferences
        return updateEmailPreferences(userId, currentPrefs);
    }
    
    /**
     * Send a test email
     */
    public boolean sendTestEmail(String userId, TestEmailRequest request) {
        try {
            // Check if the alert exists (if alertId provided)
            if (request.getAlertId() != null) {
                // Would typically verify the alert belongs to the user
            }
            
            // Get user email if no recipients specified
            List<String> recipients = request.getRecipients();
            if (recipients == null || recipients.isEmpty()) {
                String userEmail = jdbcTemplate.queryForObject(
                    "SELECT email FROM email_preferences WHERE user_id = ?",
                    String.class,
                    UUID.fromString(userId)
                );
                
                if (userEmail != null) {
                    recipients = List.of(userEmail);
                } else {
                    return false;
                }
            }
            
            // Queue test emails
            String templateId = request.getTemplateId() != null ? 
                request.getTemplateId() : "alert-notification";
                
            for (String recipient : recipients) {
                UUID emailId = UUID.randomUUID();
                jdbcTemplate.update(
                    "INSERT INTO email_queue " +
                    "(id, recipient_email, subject, body_html, template_id, status, " +
                    "alert_trigger_id, metadata) VALUES (?, ?, ?, ?, ?, ?, ?, ?::jsonb)",
                    emailId,
                    recipient,
                    "Test Email: " + templateId,
                    "<html><body><h1>Test Email</h1><p>This is a test email from Diagnyx.</p></body></html>",
                    templateId,
                    "pending",
                    request.getAlertId() != null ? UUID.fromString(request.getAlertId()) : null,
                    "{\"test\": true, \"userId\": \"" + userId + "\"}"
                );
                
                logger.info("Queued test email with ID {} for recipient {}", emailId, recipient);
            }
            
            return true;
        } catch (Exception e) {
            logger.error("Error sending test email: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get email delivery logs
     */
    public Page<EmailLogDto> getEmailLogs(String userId, String alertId, Pageable pageable) {
        try {
            StringBuilder sql = new StringBuilder(
                "SELECT l.id, q.recipient_email, q.subject, q.status, " +
                "q.sent_at, q.delivered_at, l.error_message, l.bounce_reason " +
                "FROM email_delivery_log l " +
                "JOIN email_queue q ON l.email_queue_id = q.id " +
                "WHERE q.metadata->>'userId' = ?"
            );
            
            List<Object> params = new ArrayList<>();
            params.add(userId);
            
            if (alertId != null) {
                sql.append(" AND q.alert_trigger_id = ?");
                params.add(UUID.fromString(alertId));
            }
            
            sql.append(" ORDER BY l.event_timestamp DESC LIMIT ? OFFSET ?");
            params.add(pageable.getPageSize());
            params.add(pageable.getOffset());
            
            List<EmailLogDto> logs = jdbcTemplate.query(
                sql.toString(),
                params.toArray(),
                (rs, rowNum) -> mapToEmailLogDto(rs)
            );
            
            // Count total for pagination
            String countSql = "SELECT COUNT(*) FROM email_delivery_log l " +
                              "JOIN email_queue q ON l.email_queue_id = q.id " +
                              "WHERE q.metadata->>'userId' = ?" +
                              (alertId != null ? " AND q.alert_trigger_id = ?" : "");
            
            Long total = jdbcTemplate.queryForObject(
                countSql,
                Long.class,
                alertId != null ? 
                    new Object[]{userId, UUID.fromString(alertId)} : 
                    new Object[]{userId}
            );
            
            return new PageImpl<>(logs, pageable, total != null ? total : 0);
        } catch (Exception e) {
            logger.error("Error retrieving email logs: {}", e.getMessage());
            return Page.empty();
        }
    }
    
    /**
     * Log email delivery status
     */
    public void logEmailDelivery(String userId, EmailDeliveryRequest request) {
        try {
            // Find related email queue entry or create a new one
            UUID emailQueueId = findOrCreateEmailQueueEntry(request, userId);
            
            // Log the delivery status
            UUID logId = UUID.randomUUID();
            jdbcTemplate.update(
                "INSERT INTO email_delivery_log " +
                "(id, email_queue_id, delivery_status, event_timestamp, error_message) " +
                "VALUES (?, ?, ?, now(), ?)",
                logId,
                emailQueueId,
                request.getStatus(),
                request.getErrorMessage()
            );
            
            logger.info("Logged email delivery status {} for email {}", 
                      request.getStatus(), emailQueueId);
            
            // Update the email queue status if needed
            if ("delivered".equals(request.getStatus())) {
                jdbcTemplate.update(
                    "UPDATE email_queue SET status = 'delivered', delivered_at = now() " +
                    "WHERE id = ?",
                    emailQueueId
                );
            } else if ("failed".equals(request.getStatus())) {
                jdbcTemplate.update(
                    "UPDATE email_queue SET status = 'failed', error_message = ? " +
                    "WHERE id = ?",
                    request.getErrorMessage(),
                    emailQueueId
                );
            }
        } catch (Exception e) {
            logger.error("Error logging email delivery: {}", e.getMessage());
        }
    }
    
    /**
     * Find existing email queue entry or create a new one for logging
     */
    private UUID findOrCreateEmailQueueEntry(EmailDeliveryRequest request, String userId) {
        try {
            // Try to find an existing email for this alert and recipient
            UUID emailQueueId = null;
            
            if (request.getAlertId() != null) {
                emailQueueId = jdbcTemplate.queryForObject(
                    "SELECT id FROM email_queue " +
                    "WHERE alert_trigger_id = ? AND recipient_email = ? " +
                    "ORDER BY created_at DESC LIMIT 1",
                    UUID.class,
                    UUID.fromString(request.getAlertId()),
                    request.getRecipientEmail()
                );
            }
            
            // Create a new entry if not found
            if (emailQueueId == null) {
                emailQueueId = UUID.randomUUID();
                jdbcTemplate.update(
                    "INSERT INTO email_queue " +
                    "(id, recipient_email, subject, body_html, template_id, status, " +
                    "alert_trigger_id, metadata) VALUES (?, ?, ?, ?, ?, ?, ?, ?::jsonb)",
                    emailQueueId,
                    request.getRecipientEmail(),
                    "Alert Notification",
                    "<html><body><h1>Alert Notification</h1><p>Alert notification content.</p></body></html>",
                    "alert-notification",
                    request.getStatus(),
                    request.getAlertId() != null ? UUID.fromString(request.getAlertId()) : null,
                    "{\"userId\": \"" + userId + "\"}"
                );
            }
            
            return emailQueueId;
        } catch (Exception e) {
            logger.error("Error finding or creating email queue entry: {}", e.getMessage());
            // Create a new one if anything fails
            UUID emailQueueId = UUID.randomUUID();
            jdbcTemplate.update(
                "INSERT INTO email_queue " +
                "(id, recipient_email, subject, body_html, template_id, status, metadata) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?::jsonb)",
                emailQueueId,
                request.getRecipientEmail(),
                "Email Tracking Entry",
                "<html><body><p>Email tracking entry.</p></body></html>",
                "system",
                request.getStatus(),
                "{\"userId\": \"" + userId + "\", \"manual_log\": true}"
            );
            return emailQueueId;
        }
    }
    
    /**
     * Map database result to EmailPreferencesDto
     */
    private EmailPreferencesDto mapToEmailPreferencesDto(ResultSet rs) throws SQLException {
        EmailPreferencesDto dto = new EmailPreferencesDto();
        dto.setId(UUID.fromString(rs.getString("id")));
        dto.setEmail(rs.getString("email"));
        dto.setFormat(rs.getString("format"));
        dto.setFrequency(rs.getString("frequency"));
        dto.setActive(rs.getBoolean("is_active"));
        dto.setVerified(rs.getBoolean("is_verified"));
        dto.setQuietHoursEnabled(rs.getBoolean("quiet_hours_enabled"));
        dto.setQuietHoursStart(rs.getObject("quiet_hours_start", LocalTime.class));
        dto.setQuietHoursEnd(rs.getObject("quiet_hours_end", LocalTime.class));
        dto.setSeverityFilter(rs.getString("severity_filter"));
        dto.setTimezone(rs.getString("timezone"));
        return dto;
    }
    
    /**
     * Map database result to EmailLogDto
     */
    private EmailLogDto mapToEmailLogDto(ResultSet rs) throws SQLException {
        EmailLogDto dto = new EmailLogDto();
        dto.setId(UUID.fromString(rs.getString("id")));
        dto.setStatus(rs.getString("status"));
        dto.setRecipient(rs.getString("recipient_email"));
        dto.setSubject(rs.getString("subject"));
        dto.setSentAt(rs.getObject("sent_at", ZonedDateTime.class));
        dto.setDeliveredAt(rs.getObject("delivered_at", ZonedDateTime.class));
        dto.setErrorMessage(rs.getString("error_message"));
        dto.setBounceReason(rs.getString("bounce_reason"));
        return dto;
    }

    /**
     * Get all active email templates
     */
    public List<EmailTemplateDto> getEmailTemplates() {
        try {
            return jdbcTemplate.query(
                "SELECT id, template_type, template_name, subject_template, " +
                "body_template, template_version, is_active " +
                "FROM email_templates WHERE is_active = true " +
                "ORDER BY template_type ASC",
                (rs, rowNum) -> mapToEmailTemplateDto(rs)
            );
        } catch (Exception e) {
            logger.error("Error fetching email templates: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch email templates", e);
        }
    }

    /**
     * Get a specific email template by type
     */
    public EmailTemplateDto getEmailTemplateByType(String templateType) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT id, template_type, template_name, subject_template, " +
                "body_template, template_version, is_active " +
                "FROM email_templates " +
                "WHERE template_type = ? AND is_active = true " +
                "ORDER BY template_version DESC LIMIT 1",
                (rs, rowNum) -> mapToEmailTemplateDto(rs),
                templateType
            );
        } catch (Exception e) {
            logger.error("Error fetching email template {}: {}", templateType, e.getMessage());
            throw new RuntimeException("Failed to fetch email template", e);
        }
    }

    /**
     * Queue an alert email
     */
    public UUID queueAlertEmail(String userId, String alertTriggerId, 
                               String recipientEmail, String templateType) {
        try {
            // Would normally use a stored procedure for this, but simulating it here
            // 1. Get the template
            EmailTemplateDto template = getEmailTemplateByType(templateType);
            
            // 2. Get alert details for template variables
            Map<String, Object> alertData = jdbcTemplate.queryForMap(
                "SELECT a.name AS alert_name, a.description, at.triggered_at, " +
                "at.threshold_value, at.severity " +
                "FROM alert_triggers at " +
                "JOIN alerts a ON at.alert_id = a.id " +
                "WHERE at.id = ?",
                UUID.fromString(alertTriggerId)
            );
            
            // 3. Render templates with variables
            String subject = renderTemplate(template.getSubjectTemplate(), alertData);
            String bodyHtml = renderTemplate(template.getBodyTemplate(), alertData);
            
            // 4. Create queue entry
            UUID emailId = UUID.randomUUID();
            jdbcTemplate.update(
                "INSERT INTO email_queue " +
                "(id, alert_trigger_id, recipient_email, template_id, subject, " +
                "body_html, status, priority, scheduled_at, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, now(), now())",
                emailId,
                UUID.fromString(alertTriggerId),
                recipientEmail,
                template.getId().toString(),
                subject,
                bodyHtml,
                "pending",
                10, // High priority for alerts
                new Object[]{} // Using default of current timestamp
            );
            
            return emailId;
        } catch (Exception e) {
            logger.error("Error queuing alert email: {}", e.getMessage());
            throw new RuntimeException("Failed to queue alert email", e);
        }
    }

    /**
     * Get email queue items with optional status filter
     */
    public List<EmailQueueDto> getEmailQueue(String userId, String status) {
        try {
            StringBuilder sql = new StringBuilder(
                "SELECT q.id, q.alert_trigger_id, q.recipient_email, " +
                "q.template_id, q.subject, q.body_html, q.body_text, " +
                "q.status, q.priority, q.scheduled_at, q.retry_count, q.max_retries " +
                "FROM email_queue q " +
                "JOIN alert_triggers at ON q.alert_trigger_id = at.id " +
                "JOIN alerts a ON at.alert_id = a.id " +
                "WHERE a.user_id = ?"
            );
            
            List<Object> params = new ArrayList<>();
            params.add(UUID.fromString(userId));
            
            if (status != null && !status.isEmpty()) {
                sql.append(" AND q.status = ?");
                params.add(status);
            }
            
            sql.append(" ORDER BY q.priority ASC, q.scheduled_at ASC");
            
            return jdbcTemplate.query(
                sql.toString(),
                params.toArray(),
                (rs, rowNum) -> mapToEmailQueueDto(rs)
            );
        } catch (Exception e) {
            logger.error("Error fetching email queue: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch email queue", e);
        }
    }

    /**
     * Get email delivery stats for a user
     */
    public EmailDeliveryStatsDto getEmailDeliveryStats(String userId) {
        try {
            String sql = 
                "SELECT q.status " +
                "FROM email_queue q " +
                "JOIN alert_triggers at ON q.alert_trigger_id = at.id " +
                "JOIN alerts a ON at.alert_id = a.id " +
                "WHERE a.user_id = ?";
            
            List<String> statuses = jdbcTemplate.queryForList(sql, String.class, UUID.fromString(userId));
            
            // Calculate stats
            int totalSent = 0;
            int totalDelivered = 0;
            int totalBounced = 0;
            int totalFailed = 0;
            
            for (String status : statuses) {
                switch (status) {
                    case "sent":
                        totalSent++;
                        break;
                    case "delivered":
                        totalDelivered++;
                        break;
                    case "bounced":
                        totalBounced++;
                        break;
                    case "failed":
                        totalFailed++;
                        break;
                }
            }
            
            int totalProcessed = totalSent + totalDelivered + totalBounced + totalFailed;
            double deliveryRate = totalProcessed > 0 ? (double) totalDelivered / totalProcessed * 100 : 0;
            double bounceRate = totalProcessed > 0 ? (double) totalBounced / totalProcessed * 100 : 0;
            
            EmailDeliveryStatsDto stats = new EmailDeliveryStatsDto();
            stats.setTotalSent(totalSent);
            stats.setTotalDelivered(totalDelivered);
            stats.setTotalBounced(totalBounced);
            stats.setTotalFailed(totalFailed);
            stats.setDeliveryRate(deliveryRate);
            stats.setBounceRate(bounceRate);
            
            return stats;
        } catch (Exception e) {
            logger.error("Error calculating email delivery stats: {}", e.getMessage());
            throw new RuntimeException("Failed to calculate email delivery stats", e);
        }
    }

    /**
     * Track email event (opened, clicked, etc.)
     */
    public void trackEmailEvent(String emailQueueId, String eventType, Map<String, Object> metadata) {
        try {
            jdbcTemplate.update(
                "INSERT INTO email_tracking " +
                "(id, email_queue_id, tracking_type, user_agent, ip_address, click_url, timestamp) " +
                "VALUES (?, ?, ?, ?, ?, ?, now())",
                UUID.randomUUID(),
                UUID.fromString(emailQueueId),
                eventType,
                metadata.get("userAgent"),
                metadata.get("ipAddress"),
                metadata.get("clickUrl")
            );
        } catch (Exception e) {
            logger.error("Error tracking email event: {}", e.getMessage());
            throw new RuntimeException("Failed to track email event", e);
        }
    }

    /**
     * Helper method to render templates
     */
    private String renderTemplate(String template, Map<String, Object> variables) {
        String result = template;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            result = result.replace(placeholder, entry.getValue() != null ? entry.getValue().toString() : "");
        }
        return result;
    }

    // Add mapping function for EmailTemplateDto
    private EmailTemplateDto mapToEmailTemplateDto(ResultSet rs) throws SQLException {
        EmailTemplateDto dto = new EmailTemplateDto();
        dto.setId(UUID.fromString(rs.getString("id")));
        dto.setTemplateType(rs.getString("template_type"));
        dto.setTemplateName(rs.getString("template_name"));
        dto.setSubjectTemplate(rs.getString("subject_template"));
        dto.setBodyTemplate(rs.getString("body_template"));
        dto.setTemplateVersion(rs.getInt("template_version"));
        dto.setActive(rs.getBoolean("is_active"));
        return dto;
    }

    // Add mapping function for EmailQueueDto
    private EmailQueueDto mapToEmailQueueDto(ResultSet rs) throws SQLException {
        EmailQueueDto dto = new EmailQueueDto();
        dto.setId(UUID.fromString(rs.getString("id")));
        
        if (rs.getString("alert_trigger_id") != null) {
            dto.setAlertTriggerId(UUID.fromString(rs.getString("alert_trigger_id")));
        }
        
        dto.setRecipientEmail(rs.getString("recipient_email"));
        dto.setTemplateId(rs.getString("template_id"));
        dto.setSubject(rs.getString("subject"));
        dto.setBodyHtml(rs.getString("body_html"));
        dto.setBodyText(rs.getString("body_text"));
        dto.setStatus(rs.getString("status"));
        dto.setPriority(rs.getInt("priority"));
        
        if (rs.getTimestamp("scheduled_at") != null) {
            dto.setScheduledAt(rs.getTimestamp("scheduled_at").toInstant().atZone(ZoneId.systemDefault()));
        }
        
        dto.setRetryCount(rs.getInt("retry_count"));
        dto.setMaxRetries(rs.getInt("max_retries"));
        
        return dto;
    }
} 