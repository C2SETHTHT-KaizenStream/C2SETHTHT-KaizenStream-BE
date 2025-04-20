package com.example.KaizenStream_BE.controller;


import com.example.KaizenStream_BE.dto.request.email.EmailRequest;
import com.example.KaizenStream_BE.dto.request.report.BanRequest;
import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.report.*;
import com.example.KaizenStream_BE.entity.Report;
import com.example.KaizenStream_BE.service.EmailService;
import com.example.KaizenStream_BE.service.ReportService;
import jakarta.mail.MessagingException;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportController {

    ReportService reportService;
    EmailService emailService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ApiResponse<ReportFormResponse> createReport(
            @RequestParam("reportType") String reportType,
            @RequestParam("description") String description,
            @RequestParam("userId") String userId,
            @RequestParam("livestreamId") String livestreamId,
            @RequestParam(value = "images", required = false) MultipartFile[] images) {

        try {
            ReportFormResponse report = reportService.createReport(reportType, description, userId,livestreamId, images);

            return
                    ApiResponse.<ReportFormResponse>builder()
                            .code(1000)
                            .message("true")
                            .result(report)
                            .build();


        } catch (RuntimeException e) {
            return ApiResponse.<ReportFormResponse>builder()
                            .code(4000)
                            .message(e.getMessage())
                            .build();

        } catch (IOException e) {
            return ApiResponse.<ReportFormResponse>builder()
                            .code(5000)
                            .message("Error uploading images: " + e.getMessage())
                            .build();
        }
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

    @PostMapping("/warn/{reportId}")
    public ApiResponse<ReportActionResponse> warn(@PathVariable String reportId){
        return ApiResponse.<ReportActionResponse>builder()
                .code(200)
                .message("Warn user successfully !")
                .result(reportService.warn(reportId))
                .build();
    }
    @PostMapping("/reject/{reportId}")
    public ApiResponse<ReportActionResponse> reject(@PathVariable String reportId){
        return ApiResponse.<ReportActionResponse>builder()
                .code(200)
                .message("Reject user successfully !")
                .result(reportService.reject(reportId))
                .build();
    }

    @PostMapping("/ban/{reportId}")
    public ApiResponse<ReportActionResponse> ban(@PathVariable String reportId, @RequestBody BanRequest banRequest) throws MessagingException{
        // Lấy dữ liệu từ BanRequest
        String banReason = banRequest.getBanReason();
        LocalDateTime banDuration = banRequest.getBanDuration();
        ReportActionResponse result = reportService.ban(reportId, banDuration);
        emailService.sendHtmlEmail(reportId,banReason, banDuration);
        return ApiResponse.<ReportActionResponse>builder()
                .code(200)
                .message("User has been banned successfully !")
                .result(result)
                .build();
    }



    //Lấy notification report for admin
    @GetMapping("/notify")
    public ApiResponse<List<ReportNotifyResponse>> getNotifications(){
        List<ReportNotifyResponse> report = reportService.getNotifications();
        return ApiResponse.<List<ReportNotifyResponse>>builder()
                .code(200)
                .message("Get all report notification successfully !")
                .result(report)
                .build();
    }

    @GetMapping("/ban-duration/{id}")
    public ApiResponse<LocalDateTime> getdurationBan(@PathVariable String id){
        return ApiResponse.<LocalDateTime>builder()
                .code(200)
                .message("Succcessfully")
                .result(reportService.getDurationban(id))
                .build();
    }







}
