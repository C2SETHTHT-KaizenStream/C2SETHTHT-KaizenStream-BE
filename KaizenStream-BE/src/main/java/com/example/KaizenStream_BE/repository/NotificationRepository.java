package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Chat;
import com.example.KaizenStream_BE.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Repository
public interface NotificationRepository extends JpaRepository<Notification,String> {
    List<Notification> findByUser_UserIdAndIsReadFalse(String userId);
    // Tìm thông báo có livestreamId và createAt trong phạm vi giữa start và end
    List<Notification> findByLivestream_LivestreamIdAndCreateAtBetween(String livestreamId, LocalDateTime start, LocalDateTime end);
}
