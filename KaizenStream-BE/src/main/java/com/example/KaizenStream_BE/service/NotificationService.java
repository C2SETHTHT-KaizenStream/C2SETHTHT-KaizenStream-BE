package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.respone.notification.NotificationResponse;
import com.example.KaizenStream_BE.dto.respone.report.ReportListResponse;
import com.example.KaizenStream_BE.entity.Notification;
import com.example.KaizenStream_BE.entity.Report;
import com.example.KaizenStream_BE.repository.NotificationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    NotificationRepository notificationRepository;

    public List<NotificationResponse> getNotifcationsByUser(String id) {
        // Lấy danh sách notification từ database
        List<Notification> notifications = notificationRepository.findByUser_UserIdAndIsReadFalse(id);

        // Chuyển đổi các entity Notification thành NotificationResponse
        return notifications.stream()
                .map(notification -> NotificationResponse.builder()
                        .livestreamId(notification.getLivestream() != null ? notification.getLivestream().getLivestreamId() : null)
                        .senderAvatar(notification.getSenderAvatar())
                        .senderName(notification.getSenderName())
                        .content(notification.getContent())
                        .isRead(notification.isRead())
                        .createAt(notification.getCreateAt())
                        .build())
                .collect(Collectors.toList());
    }

    public List<NotificationResponse> markAsRead(List<NotificationResponse> notifications) {
        List<Notification> processedNotifications = new ArrayList<>();
        for (NotificationResponse notify : notifications) {
            // Tính toán phạm vi thời gian cho tìm kiếm
            LocalDateTime start = notify.getCreateAt().minus(1, ChronoUnit.SECONDS);
            LocalDateTime end = notify.getCreateAt().plus(1, ChronoUnit.SECONDS);

            // Gọi repository để tìm thông báo gần với thời gian
            List<Notification> notificationsFound = notificationRepository.findByLivestream_LivestreamIdAndCreateAtBetween(
                    notify.getLivestreamId(), start, end);

            if (!notificationsFound.isEmpty()) {
                // Lấy thông báo đầu tiên trong danh sách (hoặc xử lý thêm nếu cần nhiều hơn một)
                Notification notification = notificationsFound.get(0);

                // Cập nhật trạng thái đọc
                notification.setRead(true);
                notificationRepository.save(notification);
                processedNotifications.add(notification);
            }
        }
        return notifications;
    }
}
