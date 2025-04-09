package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.request.report.ReportRequest;
import com.example.KaizenStream_BE.dto.respone.report.ReportResponse;
import com.example.KaizenStream_BE.entity.Notification;
import com.example.KaizenStream_BE.entity.Profile;
import com.example.KaizenStream_BE.entity.Report;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.enums.ErrorCode;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.repository.NotificationRepository;
import com.example.KaizenStream_BE.repository.ProfileRepository;
import com.example.KaizenStream_BE.repository.ReportRepository;
import com.example.KaizenStream_BE.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportService {
    ReportRepository reportRepository;
    CloudinaryService cloudinaryService;
    UserRepository userRepository;
    SimpMessagingTemplate messagingTemplate;
    ProfileRepository profileRepository;
    NotificationRepository notificationRepository;
    /**
     * Tạo một report mới
     */
    public Report createReport(String reportType, String description, String userId, MultipartFile[] images) throws IOException {

        //Upload nhiều ảnh và lấy URL
        List<String> imageUrls = images != null && images.length > 0
                ? cloudinaryService.uploadMultipleImages(images)
                : null;
        User user=userRepository.findById(userId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXIST));;
        Profile profile = profileRepository.findByUser_UserId(userId).orElseThrow(()-> new AppException(ErrorCode.PROFILES_NOT_EXIST));
        ReportResponse notification = new ReportResponse(profile.getAvatarUrl(),user.getUserName(), LocalDateTime.now());
        messagingTemplate.convertAndSend("/topic/adminNotifications", notification);
        // Tạo và lưu Report
        Report report = new Report();
        report.setReportType(reportType);
        report.setDescription(description);
        report.setImages(imageUrls);
        report.setUser(user); // Gán User cho Report
        report.setCreatedAt(LocalDateTime.now());
        //Tạo và lưu thông baó
        Notification notify = new Notification();
        notify.setUser(user);
        notify.setCreateAt(LocalDateTime.now());
        notify.setRead(false);
        notify.setContent(reportType);
        notificationRepository.save(notify);
        return reportRepository.save(report);
    }
}
