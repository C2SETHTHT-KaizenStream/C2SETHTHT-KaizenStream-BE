package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.chart.MonthValue;
import com.example.KaizenStream_BE.dto.respone.notification.NotificationResponse;
import com.example.KaizenStream_BE.dto.respone.report.*;
import com.example.KaizenStream_BE.entity.*;
import com.example.KaizenStream_BE.enums.AccountStatus;
import com.example.KaizenStream_BE.enums.ErrorCode;
import com.example.KaizenStream_BE.enums.ReportStatus;
import com.example.KaizenStream_BE.enums.Status;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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
    DonationRepository donationRepository;
    LivestreamRepository livestreamRepository;
    WalletRepository walletRepository;
    /**
     * Tạo một report mới
     */
    public ReportFormResponse createReport(String reportType, String description, String userId, String livestreamId, MultipartFile[] images) throws IOException {

        log.warn("Tạo một report mới");
        //Upload nhiều ảnh và lấy URL
        List<String> imageUrls = images != null && images.length > 0
                ? cloudinaryService.uploadMultipleImages(images)
                : null;
        User user=userRepository.findById(userId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXIST));;
        Profile profile = profileRepository.findByUser_UserId(userId).orElseThrow(()-> new AppException(ErrorCode.PROFILES_NOT_EXIST));

        Livestream stream = livestreamRepository.findById(livestreamId)
                .orElseThrow(() -> new AppException(ErrorCode.LIVESTREAM_NOT_EXIST));
        LocalDateTime now = LocalDateTime.now();
        // Tạo và lưu Report
        Report report = new Report();
        report.setReportType(reportType);
        report.setDescription(description);
        report.setImages(imageUrls);
        report.setStream(stream);
        report.setUser(user);
        report.setCreatedAt(now);
        Report res = reportRepository.save(report);

        //Tạo và lưu thông baó
        Notification notify = new Notification();
        notify.setUser(user);
        notify.setCreateAt(now);
        notify.setRead(false);
        notify.setContent(reportType);
        notificationRepository.save(notify);

        ReportResponse notification = new ReportResponse(res.getReportId(),profile.getAvatarUrl(),user.getUserName(), LocalDateTime.now());
        messagingTemplate.convertAndSend("/topic/adminNotifications", notification);

        // Tạo ReportFormResponse để trả về
        ReportFormResponse ref = new ReportFormResponse();
        ref.setReportId(report.getReportId());
        ref.setReportType(reportType);
        ref.setDescription(description);
        ref.setImages(imageUrls);
        ref.setLivestreamId(stream.getLivestreamId());
        ref.setUserId(user.getUserId());
        ref.setCreatedAt(now);
        return ref;
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
                .livestreamId(report.getStream().getLivestreamId())
                .description(report.getDescription())
                .createdAt(report.getCreatedAt())
                .streamerName(report.getStream().getUser().getUserName())
                .userName(report.getUser().getUserName())
                .images(report.getImages())
                .status(report.getStatus())
                .build();
    }

    public ReportActionResponse warn(String reportId) {
        Report report = reportRepository.findById(reportId).orElseThrow(()-> new AppException(ErrorCode.REPORT_NOT_EXIST));
        report.setStatus(ReportStatus.WARNED);
        reportRepository.save(report);
        String streamer = report.getStream().getUser().getUserId();

        // Lưu nội dung cảnh báo
        Notification notification = new Notification();
        notification.setUser(report.getStream().getUser());
        notification.setContent("Your livestream has been reported for: "+ report.getReportType());
        notification.setSenderAvatar("https://res.cloudinary.com/dpu7db88i/image/upload/v1744612916/v8fmrupdsepa3zqomxfa.png");
        notification.setCreateAt(LocalDateTime.now());
        notification.setRead(false);
        notification.setLivestream(report.getStream());
        notification.setSenderName("Admin");
        notificationRepository.save(notification);

        //Tạo 1 thông báo gửi đến streamer
        NotificationResponse notify = new NotificationResponse();
        notify.setSenderName("Admin");
        notify.setSenderAvatar("https://res.cloudinary.com/dpu7db88i/image/upload/v1744612916/v8fmrupdsepa3zqomxfa.png");
        notify.setContent("Your livestream has been reported for: "+ report.getReportType());
        notify.setCreateAt(LocalDateTime.now());
        notify.setRead(false);
        notify.setLivestreamId(report.getStream().getLivestreamId());

        //Thu hồi tiền donate từ phiên livestream
        List<Donation> donations = donationRepository.findByLivestream_LivestreamId(report.getStream().getLivestreamId());
        // Tính tổng số tiền cần hoàn trả
        int totalRefundAmount = 0;
        for(Donation donation : donations){
            totalRefundAmount += donation.getPointSpent();
            // Hoàn trả tiền cho người donate
//            refundDonation(donation);
//            // Gửi thông báo cho người donate rằng tiền đã được hoàn trả
//            sendRefundNotification(donation);
        }
        // Tìm ví của streamer
        Optional<Wallet> walletOptional = walletRepository.findByUser_UserId(streamer);
        if (walletOptional.isPresent()) {
            Wallet wallet = walletOptional.get(); // Lấy ví từ Optional
            int newBalance = wallet.getBalance() - totalRefundAmount;
            wallet.setBalance(newBalance);
            walletRepository.save(wallet);
        } else {
           throw new AppException(ErrorCode.WALLET_NOT_EXIST);
        }
        Livestream livestream = livestreamRepository.findById(report.getStream().getLivestreamId()).orElseThrow(()-> new AppException(ErrorCode.LIVESTREAM_NOT_EXIST));
        livestream.setStatus(Status.BANNED.getDescription());
        livestreamRepository.save(livestream);
        //Gửi thông báo đến streamer
        messagingTemplate.convertAndSend("/topic/report/" + streamer, notify);
        //Gửi thông báo để khóa livestream
        messagingTemplate.convertAndSend("/topic/report/" + report.getStream().getLivestreamId(), notify);
        ReportActionResponse response = new ReportActionResponse();
        response.setReportId(report.getReportId());
        response.setStatus(report.getStatus());
        return response;
    }

    public void refundDonation(Donation donation) {
        // Lấy ví của người donate
        Optional<Wallet> userWallet = walletRepository.findByUser_UserId(donation.getUser().getUserId());

        // Kiểm tra xem ví có tồn tại không
        if (userWallet.isPresent()) {
            Wallet wallet = userWallet.get();
            int refundedAmount = donation.getPointSpent();
            int newBalance = wallet.getBalance() + refundedAmount;  // Cộng tiền vào ví
            wallet.setBalance(newBalance);
            walletRepository.save(wallet);
        }
    }

    public void sendRefundNotification(Donation donation) {
        Notification notification = new Notification();
        notification.setUser(donation.getUser());
        notification.setContent("You have been refunded for your donation to the livestream: " + donation.getLivestream().getLivestreamId());
        notification.setSenderAvatar("https://res.cloudinary.com/dpu7db88i/image/upload/v1744612916/v8fmrupdsepa3zqomxfa.png");
        notification.setCreateAt(LocalDateTime.now());
        notification.setRead(false);
        notification.setSenderName("Admin");

        notificationRepository.save(notification);

        // Tạo phản hồi thông báo cho người donate (bạn có thể sử dụng hệ thống messaging như đã làm trước đó)
        NotificationResponse userNotify = new NotificationResponse();
        userNotify.setSenderName("Admin");
        userNotify.setSenderAvatar("https://res.cloudinary.com/dpu7db88i/image/upload/v1744612916/v8fmrupdsepa3zqomxfa.png");
        userNotify.setContent("Your donation has been refunded due to violation of the livestream rules.");
        userNotify.setCreateAt(LocalDateTime.now());
        userNotify.setRead(false);
        userNotify.setLivestreamId(donation.getLivestream().getLivestreamId());

        // Gửi thông báo đến người dùng
        messagingTemplate.convertAndSend("/topic/report/" + donation.getUser().getUserId(), userNotify);
    }

    public ReportActionResponse reject(String reportId) {
        Report report = reportRepository.findById(reportId).orElseThrow(()-> new AppException(ErrorCode.REPORT_NOT_EXIST));
        report.setStatus(ReportStatus.REJECTED);
        reportRepository.save(report);
        ReportActionResponse response = new ReportActionResponse();
        response.setReportId(report.getReportId());
        response.setStatus(report.getStatus());
        return response;
    }

    public ReportActionResponse ban(String reportId, LocalDateTime banDuration) {
        Report report = reportRepository.findById(reportId).orElseThrow(()-> new AppException(ErrorCode.REPORT_NOT_EXIST));
        report.setStatus(ReportStatus.BANNED);
        reportRepository.save(report);
        //Khóa tài khoản vĩnh viễn
        User user = userRepository.findById(report.getStream().getUser().getUserId()).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXIST));
        user.setStatus(AccountStatus.BANNED);
        user.setBanUntil(banDuration);
        userRepository.save(user);

        // Tìm tất cả các báo cáo khác của livestream có cùng streamId và set trạng thái BANNED
        List<Report> relatedReports = reportRepository.findByStream_LivestreamId(report.getStream().getLivestreamId());
        for (Report r : relatedReports) {
            // Cập nhật trạng thái các báo cáo liên quan thành BANNED
            if (r.getStatus() != ReportStatus.BANNED) {
                r.setStatus(ReportStatus.BANNED);
                reportRepository.save(r);
            }
        }

        ReportActionResponse response = new ReportActionResponse();
        response.setReportId(report.getReportId());
        response.setStatus(report.getStatus());

        Livestream livestream = livestreamRepository.findById(report.getStream().getLivestreamId()).orElseThrow(()-> new AppException(ErrorCode.LIVESTREAM_NOT_EXIST));
        livestream.setStatus(Status.BANNED.getDescription());
        livestreamRepository.save(livestream);
        //Gửi thông báo để khóa livestream
        messagingTemplate.convertAndSend("/topic/report/" + report.getStream().getLivestreamId(), "Live Stream has been locked");

        //dùng để gửi thông báo tới streamer giúp tắt livestream đã bị banned
        messagingTemplate.convertAndSend("/topic/live/banned/" + report.getStream().getLivestreamId(), "Your account has been banned !");
        System.out.println("/topic/live/banned/" + report.getStream().getLivestreamId());
        return response;
    }


    public List<ReportNotifyResponse> getNotifications() {
        List<Report> reports = reportRepository.findAll();
        return reports.stream()
                .map(report -> {
                    Profile profile = profileRepository.findByUser_UserId(report.getUser().getUserId()).orElseThrow(()-> new AppException(ErrorCode.PROFILES_NOT_EXIST));
                    String avatarUrl = profile != null ? profile.getAvatarUrl() : null;
                    return new ReportNotifyResponse(
                            report.getReportId(),
                            avatarUrl,
                            report.getUser().getUserName(),
                            report.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());
    }

    public LocalDateTime getDurationban(String id) {
        User user = userRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXIST));
        LocalDateTime duration = user.getBanUntil();
        return duration;
    }

    public List<ReportResponse> getListReport() {
        List<Report> reports = reportRepository.findAll(Sort.by(Sort.Order.desc("createdAt")));
        return reports.stream()
                .map(

                        report->{
                            Profile profile = profileRepository.findByUser_UserId(report.getUser().getUserId()).orElseThrow(()-> new AppException(ErrorCode.PROFILES_NOT_EXIST));
                            return new ReportResponse(
                                   report.getReportId(),
                                    profile.getAvatarUrl(),
                                    report.getUser().getUserName(),
                                    report.getCreatedAt()
                            );
                        }
                )
                .collect(Collectors.toList());
    }

    public ApiResponse<List<MonthValue>> getReportCountByMonth() {
        try {
            // Lấy kết quả từ Repository
            List<Object[]> results = reportRepository.findReportCountByMonth();

            // Nếu không có dữ liệu
            if (results.isEmpty()) {
                return ApiResponse.<List<MonthValue>>builder()
                        .code(1001)
                        .message("No reports found")
                        .result(null)
                        .status("ERROR")
                        .build();  // Trả về lỗi 1001 nếu không có kết quả
            }

            // Chuyển đổi List<Object[]> thành List<MonthValue>
            List<MonthValue> monthValues = new ArrayList<>();
            for (Object[] entry : results) {
                String month = (String) entry[0];  // Lấy tên tháng
                int value = (Integer) entry[1];    // Lấy số báo cáo
                monthValues.add(new MonthValue(month, value));  // Thêm vào danh sách
            }

            // Trả về dữ liệu thành công
            return ApiResponse.<List<MonthValue>>builder()
                    .code(1000)
                    .message("Reports fetched successfully")
                    .result(monthValues)
                    .status("SUCCESS")
                    .build();  // Trả về kết quả thành công

        } catch (Exception e) {
            // Trả về lỗi nếu gặp ngoại lệ
            return ApiResponse.<List<MonthValue>>builder()
                    .code(1002)
                    .message("An error occurred: " + e.getMessage())
                    .result(null)
                    .status("ERROR")
                    .build();  // Trả về lỗi 1002 khi có ngoại lệ
        }
    }
}
