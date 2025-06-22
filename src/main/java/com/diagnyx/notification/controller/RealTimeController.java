package com.diagnyx.notification.controller;

import com.diagnyx.notification.service.RealTimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/realtime")
@RequiredArgsConstructor
@Slf4j
public class RealTimeController {

    private final RealTimeService realTimeService;

    /**
     * Get WebSocket connection details
     */
    @GetMapping("/connection-info")
    public ResponseEntity<Map<String, Object>> getConnectionInfo() {
        return ResponseEntity.ok(Map.of(
            "websocketUrl", "/ws",
            "stompEndpoint", "/ws",
            "topicPrefix", "/topic/",
            "userPrefix", "/user/",
            "appPrefix", "/app/"
        ));
    }

    /**
     * Get all channels for a user
     */
    @GetMapping("/channels")
    public ResponseEntity<List<String>> getUserChannels(@RequestParam String userId) {
        return ResponseEntity.ok(realTimeService.getUserChannels(userId));
    }

    /**
     * Check if a user is subscribed to a channel
     */
    @GetMapping("/subscribed")
    public ResponseEntity<Boolean> isSubscribed(
            @RequestParam String userId,
            @RequestParam String channelName) {
        return ResponseEntity.ok(realTimeService.isSubscribed(userId, channelName));
    }

    /**
     * Broadcast a message to a channel
     */
    @PostMapping("/broadcast")
    public ResponseEntity<Void> broadcastMessage(
            @RequestParam String channelName,
            @RequestBody Map<String, Object> payload) {
        realTimeService.broadcastToChannel(channelName, payload);
        return ResponseEntity.ok().build();
    }

    /**
     * Send a message to a specific user
     */
    @PostMapping("/send")
    public ResponseEntity<Void> sendToUser(
            @RequestParam String userId,
            @RequestBody Map<String, Object> payload) {
        realTimeService.sendToUser(userId, payload);
        return ResponseEntity.ok().build();
    }
} 