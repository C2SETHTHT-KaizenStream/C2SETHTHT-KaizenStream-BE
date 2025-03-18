package com.example.KaizenStream_BE.controller;


import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.entity.Report;
import com.example.KaizenStream_BE.service.ReportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
            @RequestParam(value = "images", required = false) MultipartFile[] images) {

        try {
            Report report = reportService.createReport(reportType, description, userId, images);
            return ResponseEntity.ok(
                    ApiResponse.<Report>builder()
                            .code(1000)
                            .message("success")
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

}
