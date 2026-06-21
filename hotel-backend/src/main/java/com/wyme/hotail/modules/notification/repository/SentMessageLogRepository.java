package com.wyme.hotail.modules.notification.repository;

import com.wyme.hotail.modules.notification.entity.SentMessageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SentMessageLogRepository extends JpaRepository<SentMessageLog, UUID> {
    List<SentMessageLog> findAllByOrderByCreatedAtDesc();
}
