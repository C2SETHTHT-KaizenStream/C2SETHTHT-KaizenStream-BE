package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.respone.report.ReportDetailResponse;
import com.example.KaizenStream_BE.dto.respone.report.ReportListResponse;
import com.example.KaizenStream_BE.dto.respone.report.ReportResponse;
import com.example.KaizenStream_BE.entity.*;
import com.example.KaizenStream_BE.enums.ErrorCode;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;

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
   
    LivestreamRepository livestreamRepository;
    /**
     * Tạo một report mới
     */
    public Report createReport(String reportType, String description, String userId, String livestreamId,MultipartFile[] images) throws IOException {

        //Upload nhiều ảnh và lấy URL
        List<String> imageUrls = images != null && images.length > 0
                ? cloudinaryService.uploadMultipleImages(images)
                : null;
        User user=userRepository.findById(userId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXIST));;
        Profile profile = profileRepository.findByUser_UserId(userId).orElseThrow(()-> new AppException(ErrorCode.PROFILES_NOT_EXIST));
        ReportResponse notification = new ReportResponse(profile.getAvatarUrl(),user.getUserName(), LocalDateTime.now());
        messagingTemplate.convertAndSend("/topic/adminNotifications", notification);
        Livestream stream = livestreamRepository.findById(livestreamId)
                .orElseThrow(() -> new AppException(ErrorCode.LIVESTREAM_NOT_EXIST));
        // Tạo và lưu Report
        Report report = new Report();
        report.setReportType(reportType);
        report.setDescription(description);
        report.setImages(imageUrls);
        report.setStream(stream);
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


    public List<ReportListResponse> getAllReport() {
        List<Report> reports = reportRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return reports.stream().map(report -> {
            String streamerName = "";
            if (report.getStream() != null && report.getStream().getUser() != null) {
                streamerName = report.getStream().getUser().getUserName();
            }

            return ReportListResponse.builder()
                    .reportId(report.getReportId())
                    .reportType(report.getReportType())
                    .description(report.getDescription())
                    .createdAt(report.getCreatedAt())
                    .streamerName(streamerName)
                    .status(report.getStatus())
                    .userName(report.getUser().getUserName())
                    .build();
        }).collect(Collectors.toList());
    }

    public ReportDetailResponse getReportDetail(String reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_EXIST));

        return ReportDetailResponse.builder()
                .description(report.getDescription())
                .createdAt(report.getCreatedAt())
                .streamerName(report.getStream().getUser().getUserName())
                .userName(report.getUser().getUserName())
                .images(report.getImages())
                .build();
    }
}
