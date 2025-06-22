package com.diagnyx.notification.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller for Notification Service
 * 
 * Provides health status and database connectivity checks
 */
@RestController
public class HealthController {

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Service status endpoint for database connectivity check
     */
    @GetMapping("/service-status")
    public ResponseEntity<Map<String, Object>> serviceStatus() {
        logger.info("Service status check requested");
        
        Map<String, Object> status = new HashMap<>();
        status.put("service", "diagnyx-notification-service");
        status.put("timestamp", LocalDateTime.now().toString());
        
        boolean dbHealthy = checkDatabaseConnection();
        status.put("database", dbHealthy ? "UP" : "DOWN");
        status.put("status", dbHealthy ? "SUCCESS" : "FAILURE");
        status.put("message", dbHealthy ? 
                "Notification Service is operational with database connectivity" : 
                "Notification Service is degraded - database connectivity issue");
        
        logger.info("Service status check completed - Status: {}", status.get("status"));
        
        return ResponseEntity.ok(status);
    }

    /**
     * Check database connectivity
     */
    private boolean checkDatabaseConnection() {
        try {
            // Simple query to test database connectivity
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return result != null && result == 1;
        } catch (Exception e) {
            logger.error("Database connectivity check failed", e);
            return false;
        }
    }
} 