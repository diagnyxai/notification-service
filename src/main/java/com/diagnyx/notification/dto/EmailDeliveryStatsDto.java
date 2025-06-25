package com.diagnyx.notification.dto;

/**
 * DTO for email delivery statistics
 */
public class EmailDeliveryStatsDto {
    
    private Integer totalSent;
    private Integer totalDelivered;
    private Integer totalBounced;
    private Integer totalFailed;
    private Double deliveryRate;
    private Double bounceRate;
    
    public EmailDeliveryStatsDto() {
    }
    
    public EmailDeliveryStatsDto(Integer totalSent, Integer totalDelivered, Integer totalBounced,
                                Integer totalFailed, Double deliveryRate, Double bounceRate) {
        this.totalSent = totalSent;
        this.totalDelivered = totalDelivered;
        this.totalBounced = totalBounced;
        this.totalFailed = totalFailed;
        this.deliveryRate = deliveryRate;
        this.bounceRate = bounceRate;
    }
    
    public Integer getTotalSent() {
        return totalSent;
    }
    
    public void setTotalSent(Integer totalSent) {
        this.totalSent = totalSent;
    }
    
    public Integer getTotalDelivered() {
        return totalDelivered;
    }
    
    public void setTotalDelivered(Integer totalDelivered) {
        this.totalDelivered = totalDelivered;
    }
    
    public Integer getTotalBounced() {
        return totalBounced;
    }
    
    public void setTotalBounced(Integer totalBounced) {
        this.totalBounced = totalBounced;
    }
    
    public Integer getTotalFailed() {
        return totalFailed;
    }
    
    public void setTotalFailed(Integer totalFailed) {
        this.totalFailed = totalFailed;
    }
    
    public Double getDeliveryRate() {
        return deliveryRate;
    }
    
    public void setDeliveryRate(Double deliveryRate) {
        this.deliveryRate = deliveryRate;
    }
    
    public Double getBounceRate() {
        return bounceRate;
    }
    
    public void setBounceRate(Double bounceRate) {
        this.bounceRate = bounceRate;
    }
} 