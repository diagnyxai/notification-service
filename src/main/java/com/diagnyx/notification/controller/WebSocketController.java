package com.diagnyx.notification.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.diagnyx.notification.service.RealTimeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@Controller
@CrossOrigin
public class WebSocketController {

    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);
    
    private final SimpMessagingTemplate messagingTemplate;
    private final RealTimeService realTimeService;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public WebSocketController(SimpMessagingTemplate messagingTemplate, RealTimeService realTimeService, ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.realTimeService = realTimeService;
        this.objectMapper = objectMapper;
    }

    /**
     * Subscribe to real-time updates for a specific channel
     */
    @MessageMapping("/subscribe")
    public void subscribeToChannel(Map<String, String> payload) {
        String channelName = payload.get("channel");
        String userId = payload.get("userId");
        
        log.info("User {} subscribing to channel: {}", userId, channelName);
        realTimeService.registerSubscription(userId, channelName);
    }

    /**
     * Unsubscribe from real-time updates for a specific channel
     */
    @MessageMapping("/unsubscribe")
    public void unsubscribeFromChannel(Map<String, String> payload) {
        String channelName = payload.get("channel");
        String userId = payload.get("userId");
        
        log.info("User {} unsubscribing from channel: {}", userId, channelName);
        realTimeService.removeSubscription(userId, channelName);
    }

    /**
     * Send a heartbeat to check connection status
     */
    @MessageMapping("/heartbeat")
    @SendTo("/topic/heartbeat")
    public Map<String, Object> heartbeat(Map<String, Object> payload) {
        long timestamp = System.currentTimeMillis();
        return Map.of(
            "type", "heartbeat_response",
            "timestamp", timestamp,
            "received", payload.get("timestamp")
        );
    }

    /**
     * Manually send message to a specific topic
     */
    public void sendMessage(String destination, Object payload) {
        messagingTemplate.convertAndSend(destination, payload);
    }
} 