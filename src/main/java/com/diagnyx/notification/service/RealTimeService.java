package com.diagnyx.notification.service;

import java.util.List;
import java.util.Map;

/**
 * Service for handling real-time subscriptions and notifications
 */
public interface RealTimeService {

    /**
     * Register a user subscription to a specific channel
     * 
     * @param userId User ID
     * @param channelName Channel name
     */
    void registerSubscription(String userId, String channelName);
    
    /**
     * Remove a user subscription from a specific channel
     * 
     * @param userId User ID
     * @param channelName Channel name
     */
    void removeSubscription(String userId, String channelName);
    
    /**
     * Get all active channels for a user
     * 
     * @param userId User ID
     * @return List of channel names
     */
    List<String> getUserChannels(String userId);
    
    /**
     * Broadcast a message to all subscribers of a channel
     * 
     * @param channelName Channel name
     * @param payload Message payload
     */
    void broadcastToChannel(String channelName, Object payload);
    
    /**
     * Send a message to a specific user
     * 
     * @param userId User ID
     * @param payload Message payload
     */
    void sendToUser(String userId, Object payload);
    
    /**
     * Check if a user is subscribed to a channel
     * 
     * @param userId User ID
     * @param channelName Channel name
     * @return true if subscribed, false otherwise
     */
    boolean isSubscribed(String userId, String channelName);
} 