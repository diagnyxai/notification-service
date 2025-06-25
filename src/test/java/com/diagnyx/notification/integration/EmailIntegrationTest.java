package com.diagnyx.notification.integration;

import com.diagnyx.notification.dto.EmailDeliveryRequest;
import com.diagnyx.notification.dto.TestEmailRequest;
import com.diagnyx.notification.entity.EmailQueue;
import com.diagnyx.notification.repository.EmailQueueRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for email functionality
 */
@DisplayName("Email Integration Tests")
class EmailIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private EmailQueueRepository emailQueueRepository;

    @Test
    @DisplayName("Should successfully send test email")
    void shouldSuccessfullySendTestEmail() throws Exception {
        // Arrange
        TestEmailRequest request = new TestEmailRequest();
        request.setToEmail("test@example.com");
        request.setSubject("Integration Test Email");
        request.setBody("This is a test email from integration test");

        // Act
        ResultActions result = mockMvc.perform(post("/api/v1/email/send-test")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("sent"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.email_id").exists());
    }

    @Test
    @DisplayName("Should queue email for delivery")
    void shouldQueueEmailForDelivery() throws Exception {
        // Arrange
        EmailDeliveryRequest request = new EmailDeliveryRequest();
        request.setToEmail("user@example.com");
        request.setSubject("Alert Notification");
        request.setBody("Your API threshold has been exceeded");
        request.setUserId("123e4567-e89b-12d3-a456-426614174000");
        request.setPriority(5);

        // Act
        ResultActions result = mockMvc.perform(post("/api/v1/email/queue")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("queued"))
                .andExpect(jsonPath("$.queue_id").exists());

        // Verify email was queued in database
        Optional<EmailQueue> queuedEmail = emailQueueRepository.findAll()
                .stream()
                .filter(e -> e.getToEmail().equals("user@example.com"))
                .findFirst();

        assertTrue(queuedEmail.isPresent());
        assertEquals("Alert Notification", queuedEmail.get().getSubject());
        assertEquals("PENDING", queuedEmail.get().getStatus());
    }

    @Test
    @DisplayName("Should validate email request fields")
    void shouldValidateEmailRequestFields() throws Exception {
        // Arrange
        EmailDeliveryRequest invalidRequest = new EmailDeliveryRequest();
        invalidRequest.setToEmail("invalid-email"); // Invalid email format
        invalidRequest.setSubject(""); // Empty subject
        // Missing body

        // Act
        ResultActions result = mockMvc.perform(post("/api/v1/email/queue")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invalidRequest)));

        // Assert
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(true))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.error_code").exists());
    }

    @Test
    @DisplayName("Should get email queue status")
    void shouldGetEmailQueueStatus() throws Exception {
        // Arrange - Add some test emails to queue
        EmailQueue email1 = new EmailQueue();
        email1.setToEmail("test1@example.com");
        email1.setSubject("Test 1");
        email1.setBody("Body 1");
        email1.setStatus("PENDING");
        email1.setPriority(5);
        emailQueueRepository.save(email1);

        EmailQueue email2 = new EmailQueue();
        email2.setToEmail("test2@example.com");
        email2.setSubject("Test 2");
        email2.setBody("Body 2");
        email2.setStatus("SENT");
        email2.setPriority(3);
        emailQueueRepository.save(email2);

        // Act
        ResultActions result = mockMvc.perform(get("/api/v1/email/queue/status")
                .with(csrf()));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.total_queued").value(1))
                .andExpect(jsonPath("$.total_sent").value(1))
                .andExpect(jsonPath("$.total_failed").value(0))
                .andExpect(jsonPath("$.queue_size").value(1));
    }

    @Test
    @DisplayName("Should get email delivery stats")
    void shouldGetEmailDeliveryStats() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(get("/api/v1/email/stats")
                .with(csrf()));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.today_sent").exists())
                .andExpect(jsonPath("$.today_failed").exists())
                .andExpect(jsonPath("$.total_sent").exists())
                .andExpect(jsonPath("$.total_failed").exists())
                .andExpect(jsonPath("$.success_rate").exists());
    }

    @Test
    @DisplayName("Should handle email processing errors gracefully")
    void shouldHandleEmailProcessingErrorsGracefully() throws Exception {
        // Arrange - Create request with problematic content
        EmailDeliveryRequest request = new EmailDeliveryRequest();
        request.setToEmail("test@nonexistent-domain-12345.com");
        request.setSubject("Test Error Handling");
        request.setBody("This email should fail to send");
        request.setUserId("123e4567-e89b-12d3-a456-426614174000");

        // Act
        ResultActions result = mockMvc.perform(post("/api/v1/email/queue")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)));

        // Assert - Should queue successfully even if sending will fail later
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("queued"));
    }

    @Test
    @DisplayName("Should process email queue")
    void shouldProcessEmailQueue() throws Exception {
        // Arrange - Add emails to queue
        EmailQueue email = new EmailQueue();
        email.setToEmail("process@example.com");
        email.setSubject("Process Test");
        email.setBody("This email should be processed");
        email.setStatus("PENDING");
        email.setPriority(5);
        emailQueueRepository.save(email);

        // Act
        ResultActions result = mockMvc.perform(post("/api/v1/email-processor/process")
                .with(csrf())
                .param("batchSize", "10"));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.processed_count").exists())
                .andExpect(jsonPath("$.failed_count").exists());
    }

    @Test
    @DisplayName("Should get email logs with pagination")
    void shouldGetEmailLogsWithPagination() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(get("/api/v1/email/logs")
                .with(csrf())
                .param("page", "0")
                .param("size", "10"));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    @DisplayName("Should update email preferences")
    void shouldUpdateEmailPreferences() throws Exception {
        // Arrange
        String userId = "123e4567-e89b-12d3-a456-426614174000";
        String preferencesJson = """
            {
                "alert_notifications": true,
                "daily_reports": false,
                "marketing_emails": false,
                "security_alerts": true
            }
            """;

        // Act
        ResultActions result = mockMvc.perform(put("/api/v1/email/preferences/" + userId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(preferencesJson));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("updated"))
                .andExpect(jsonPath("$.user_id").value(userId));
    }

    @Test
    @DisplayName("Should get email preferences")
    void shouldGetEmailPreferences() throws Exception {
        // Arrange
        String userId = "123e4567-e89b-12d3-a456-426614174000";

        // Act
        ResultActions result = mockMvc.perform(get("/api/v1/email/preferences/" + userId)
                .with(csrf()));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(userId))
                .andExpect(jsonPath("$.alert_notifications").exists())
                .andExpect(jsonPath("$.daily_reports").exists())
                .andExpect(jsonPath("$.marketing_emails").exists())
                .andExpect(jsonPath("$.security_alerts").exists());
    }

    @Test
    @DisplayName("Should handle large email body content")
    void shouldHandleLargeEmailBodyContent() throws Exception {
        // Arrange
        EmailDeliveryRequest request = new EmailDeliveryRequest();
        request.setToEmail("large@example.com");
        request.setSubject("Large Content Test");
        request.setBody("A".repeat(50000)); // 50KB body
        request.setUserId("123e4567-e89b-12d3-a456-426614174000");

        // Act
        ResultActions result = mockMvc.perform(post("/api/v1/email/queue")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)));

        // Assert - Should handle large content or return appropriate error
        result.andExpect(status().isAnyOf(200, 413, 422));
    }

    @Test
    @DisplayName("Should maintain email audit trail")
    void shouldMaintainEmailAuditTrail() throws Exception {
        // Arrange
        EmailDeliveryRequest request = new EmailDeliveryRequest();
        request.setToEmail("audit@example.com");
        request.setSubject("Audit Trail Test");
        request.setBody("This email should be audited");
        request.setUserId("123e4567-e89b-12d3-a456-426614174000");

        // Act
        ResultActions queueResult = mockMvc.perform(post("/api/v1/email/queue")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)));

        // Assert queue operation
        queueResult.andExpect(status().isOk());

        // Check audit trail exists
        ResultActions auditResult = mockMvc.perform(get("/api/v1/email/logs")
                .with(csrf())
                .param("toEmail", "audit@example.com"));

        auditResult.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}