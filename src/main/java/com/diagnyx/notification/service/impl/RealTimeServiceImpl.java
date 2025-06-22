package com.diagnyx.notification.service.impl;

import com.diagnyx.notification.service.RealTimeService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class RealTimeServiceImpl implements RealTimeService {

    private final SimpMessagingTemplate messagingTemplate;
    
    // Map of userId -> List of channels
    private final Map<String, List<String>> userSubscriptions = new ConcurrentHashMap<>();
    
    // Map of channelName -> List of userIds
    private final Map<String, List<String>> channelSubscribers = new ConcurrentHashMap<>();

    @Override
    public void registerSubscription(String userId, String channelName) {
        // Add to user subscriptions
        userSubscriptions.computeIfAbsent(userId, k -> new ArrayList<>())
                        .add(channelName);
        
        // Add to channel subscribers
        channelSubscribers.computeIfAbsent(channelName, k -> new ArrayList<>())
                        .add(userId);
        
        log.info("User {} subscribed to channel {}", userId, channelName);
    }

    @Override
    public void removeSubscription(String userId, String channelName) {
        // Remove from user subscriptions
        if (userSubscriptions.containsKey(userId)) {
            userSubscriptions.get(userId).remove(channelName);
            if (userSubscriptions.get(userId).isEmpty()) {
                userSubscriptions.remove(userId);
            }
        }
        
        // Remove from channel subscribers
        if (channelSubscribers.containsKey(channelName)) {
            channelSubscribers.get(channelName).remove(userId);
            if (channelSubscribers.get(channelName).isEmpty()) {
                channelSubscribers.remove(channelName);
            }
        }
        
        log.info("User {} unsubscribed from channel {}", userId, channelName);
    }

    @Override
    public List<String> getUserChannels(String userId) {
        return userSubscriptions.getOrDefault(userId, new ArrayList<>());
    }

    @Override
    public void broadcastToChannel(String channelName, Object payload) {
        messagingTemplate.convertAndSend("/topic/" + channelName, payload);
        log.debug("Broadcast message to channel {}: {}", channelName, payload);
    }

    @Override
    public void sendToUser(String userId, Object payload) {
        messagingTemplate.convertAndSendToUser(userId, "/queue/messages", payload);
        log.debug("Sent message to user {}: {}", userId, payload);
    }

    @Override
    public boolean isSubscribed(String userId, String channelName) {
        return userSubscriptions.containsKey(userId) && 
               userSubscriptions.get(userId).contains(channelName);
    }
} 