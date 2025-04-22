package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.chart.DonationGrowthDTO;
import com.example.KaizenStream_BE.dto.respone.chart.MonthlyViewerCountDTO;
import com.example.KaizenStream_BE.dto.respone.chart.TopUserDTO;
import com.example.KaizenStream_BE.service.DonationService;
import com.example.KaizenStream_BE.service.LivestreamService;
import com.example.KaizenStream_BE.service.ReportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChartOverviewController {
    ReportService reportService;
    DonationService donationService;
    LivestreamService livestreamService;
    // API để lấy số lượng báo cáo theo tháng
    @GetMapping("/reports/monthly")
    public ApiResponse<?> getReportsByMonth() {
        // Gọi phương thức trong Service và trả về ApiResponse
        return reportService.getReportCountByMonth();
    }

    @GetMapping("/donation-growth")
    public ApiResponse<DonationGrowthDTO> getDonationGrowth() {
        try {
            // Gọi phương thức trong Service và nhận dữ liệu thô
            List<Object[]> result = donationService.getDonationGrowthPercentageData();

            // Kiểm tra nếu không có dữ liệu
            if (result.isEmpty()) {
                return ApiResponse.<DonationGrowthDTO>builder()
                        .code(1001)
                        .message("No data found")
                        .result(null)
                        .status("ERROR")
                        .build();  // Trả về lỗi nếu không có dữ liệu
            }

            // Chuyển đổi dữ liệu từ Object[] thành DTO
            Object[] data = result.get(0);
            double currentMonth = ((Number) data[0]).doubleValue();  // Ép kiểu đúng
            double lastMonth = ((Number) data[1]).doubleValue();    // Ép kiểu đúng
            double growthPercentage = ((Number) data[2]).doubleValue(); // Ép kiểu đúng

            // Tạo DTO để trả về
            DonationGrowthDTO donationGrowthDTO = new DonationGrowthDTO(growthPercentage, currentMonth);

            // Trả về ApiResponse với dữ liệu thành công
            return ApiResponse.<DonationGrowthDTO>builder()
                    .code(1000)
                    .message("Donation growth percentage fetched successfully")
                    .result(donationGrowthDTO)
                    .status("SUCCESS")
                    .build();  // Trả về DTO

        } catch (Exception e) {
            // Trả về lỗi nếu gặp ngoại lệ
            return ApiResponse.<DonationGrowthDTO>builder()
                    .code(1002)
                    .message("An error occurred: " + e.getMessage())
                    .result(null)
                    .status("ERROR")
                    .build();  // Trả về lỗi khi gặp ngoại lệ
        }
    }

    @GetMapping("/livestream-monthly-viewers")
    public ApiResponse<List<MonthlyViewerCountDTO>> getMonthlyViewerCounts() {
        // Gọi phương thức trong Service và trả về ApiResponse
        return livestreamService.getMonthlyViewerCounts();
    }
    // API để lấy Top 4 người dùng có lượt xem cao nhất
    @GetMapping("/top-users-by-view-count")
    public ApiResponse<List<TopUserDTO>> getTopUsersByViewCount() {
        // Gọi phương thức trong Service và trả về ApiResponse
        return livestreamService.getTopUsersByViewCount();
    }
}
