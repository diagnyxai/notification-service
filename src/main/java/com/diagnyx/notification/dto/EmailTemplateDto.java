package com.diagnyx.notification.dto;

import java.util.UUID;

/**
 * DTO for email template data
 */
public class EmailTemplateDto {
    
    private UUID id;
    private String templateType;
    private String templateName;
    private String subjectTemplate;
    private String bodyTemplate;
    private Integer templateVersion;
    private boolean isActive;
    
    public EmailTemplateDto() {
    }
    
    public EmailTemplateDto(UUID id, String templateType, String templateName, String subjectTemplate, 
                           String bodyTemplate, Integer templateVersion, boolean isActive) {
        this.id = id;
        this.templateType = templateType;
        this.templateName = templateName;
        this.subjectTemplate = subjectTemplate;
        this.bodyTemplate = bodyTemplate;
        this.templateVersion = templateVersion;
        this.isActive = isActive;
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getTemplateType() {
        return templateType;
    }
    
    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }
    
    public String getTemplateName() {
        return templateName;
    }
    
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
    
    public String getSubjectTemplate() {
        return subjectTemplate;
    }
    
    public void setSubjectTemplate(String subjectTemplate) {
        this.subjectTemplate = subjectTemplate;
    }
    
    public String getBodyTemplate() {
        return bodyTemplate;
    }
    
    public void setBodyTemplate(String bodyTemplate) {
        this.bodyTemplate = bodyTemplate;
    }
    
    public Integer getTemplateVersion() {
        return templateVersion;
    }
    
    public void setTemplateVersion(Integer templateVersion) {
        this.templateVersion = templateVersion;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
} 