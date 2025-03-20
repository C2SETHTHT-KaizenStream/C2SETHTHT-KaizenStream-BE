package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.entity.Report;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.repository.ReportRepository;
import com.example.KaizenStream_BE.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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

    /**
     * Tạo một report mới
     */
    public Report createReport(String reportType, String description, String userId, MultipartFile[] images) throws IOException {

        //Upload nhiều ảnh và lấy URL
        List<String> imageUrls = images != null && images.length > 0
                ? cloudinaryService.uploadMultipleImages(images)
                : null;
        User user=userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));;

        // Tạo và lưu Report
        Report report = new Report();
        report.setReportType(reportType);
        report.setDescription(description);
        report.setImages(imageUrls);
        report.setUser(user); // Gán User cho Report
        report.setCreatedAt(LocalDateTime.now());

        return reportRepository.save(report);
    }
}
