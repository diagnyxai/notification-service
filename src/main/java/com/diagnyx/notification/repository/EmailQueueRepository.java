package com.diagnyx.notification.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.diagnyx.notification.entity.EmailQueue;

@Repository
public interface EmailQueueRepository extends JpaRepository<EmailQueue, UUID> {
    
    List<EmailQueue> findByStatus(String status);
    
    List<EmailQueue> findByStatusAndScheduledAtLessThanEqual(String status, OffsetDateTime now);
    
    @Query("SELECT e FROM EmailQueue e WHERE e.status = 'pending' AND e.scheduledAt <= :now AND e.retryCount < e.maxRetries ORDER BY e.priority DESC, e.scheduledAt ASC")
    List<EmailQueue> findNextBatchToProcess(@Param("now") OffsetDateTime now, Pageable pageable);
    
    @Modifying
    @Query("UPDATE EmailQueue e SET e.status = :status, e.errorMessage = :errorMessage, e.retryCount = e.retryCount + 1 WHERE e.id = :id")
    int markAsFailed(@Param("id") UUID id, @Param("status") String status, @Param("errorMessage") String errorMessage);
    
    @Modifying
    @Query("UPDATE EmailQueue e SET e.status = :status, e.sentAt = :sentAt WHERE e.id = :id")
    int markAsSent(@Param("id") UUID id, @Param("status") String status, @Param("sentAt") OffsetDateTime sentAt);
    
    @Modifying
    @Query("UPDATE EmailQueue e SET e.status = :status, e.deliveredAt = :deliveredAt WHERE e.id = :id")
    int markAsDelivered(@Param("id") UUID id, @Param("status") String status, @Param("deliveredAt") OffsetDateTime deliveredAt);
    
    @Query("SELECT COUNT(e) FROM EmailQueue e WHERE e.status = :status")
    long countByStatus(@Param("status") String status);
    
    List<EmailQueue> findByAlertTriggerId(UUID alertTriggerId);
} 