package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Chat;
import com.example.KaizenStream_BE.entity.Notification;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface NotificationRepository extends Repository<Notification,String> {
}
