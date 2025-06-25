package com.diagnyx.notification.dto;

import java.util.List;

/**
 * DTO for test email requests
 */
public class TestEmailRequest {
    private String alertId;
    private List<String> recipients;
    private String templateId;
    private Object testData;

    // Getters and setters
    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public Object getTestData() {
        return testData;
    }

    public void setTestData(Object testData) {
        this.testData = testData;
    }
} 