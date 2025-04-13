package com.example.KaizenStream_BE.controller;


import com.example.KaizenStream_BE.dto.request.report.ReportRequest;
import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.report.ReportDetailResponse;
import com.example.KaizenStream_BE.dto.respone.report.ReportListResponse;
import com.example.KaizenStream_BE.entity.Report;
import com.example.KaizenStream_BE.service.ReportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportController {

    ReportService reportService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Report>> createReport(
            @RequestParam("reportType") String reportType,
            @RequestParam("description") String description,
            @RequestParam("userId") String userId,
            @RequestParam("livestreamId") String livestreamId,
            @RequestParam(value = "images", required = false) MultipartFile[] images) {
        // Tạo Logger
        Logger logger = LoggerFactory.getLogger(getClass());
        try {
            Report report = reportService.createReport(reportType, description, userId,livestreamId, images);

            return ResponseEntity.ok(
                    ApiResponse.<Report>builder()
                            .code(1000)
                            .message("true")
                            .result(report)
                            .build()
            );

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Report>builder()
                            .code(4000)
                            .message(e.getMessage())
                            .build()
            );
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(
                    ApiResponse.<Report>builder()
                            .code(5000)
                            .message("Error uploading images: " + e.getMessage())
                            .build()
            );
        }
    }
    @MessageMapping("/reportNotifications")
    @SendTo("/topic/adminNotifications")
    public String sendMessage(String message){
        return message;
    }

    @GetMapping("/all")
    public ApiResponse<List<ReportListResponse>> getAllReport(){
        return ApiResponse.<List<ReportListResponse>>builder()
                .code(200)
                .message("Get all reports successfully !")
                .result(reportService.getAllReport())
                .build();
    }

    @GetMapping("/detail/{reportId}")
    public ApiResponse<ReportDetailResponse> getReportDetail(@PathVariable String reportId) {
        ReportDetailResponse response = reportService.getReportDetail(reportId);
        return ApiResponse.<ReportDetailResponse>builder()
                .code(200)
                .message("Get report detail successfully !")
                .result(response)
                .build();
    }
}
