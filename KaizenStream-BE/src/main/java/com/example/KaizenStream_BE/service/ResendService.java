package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.entity.Livestream;
import com.example.KaizenStream_BE.entity.Report;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.enums.ErrorCode;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.repository.ReportRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResendService {

    @Value("${resend.api.key}")
    private String resendApiKey;  // API Key của Resend từ application.properties

    private static final String RESEND_API_URL = "https://api.resend.com/emails";  // Endpoint của Resend API

    private final RestTemplate restTemplate;
    private final ReportRepository reportRepository;

//    @PostConstruct
//    public void init() {
//        // In ra giá trị để kiểm tra
//        System.out.println("Resend API Key: " + resendApiKey);
//    }

    // Gửi email với nội dung HTML
//    @Async
    public void sendHtmlEmail(String reportId, String banReason, LocalDateTime banDuration) {
        // Tìm báo cáo trong cơ sở dữ liệu
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_EXIST));

        // Khởi tạo mối quan hệ lazy-loaded trước khi gửi email
        // Đảm bảo rằng các đối tượng liên quan đã được tải đầy đủ trước khi session đóng
        String streamerEmail = report.getStream().getUser().getEmail();
        String streamerName = report.getStream().getUser().getUserName();

        // Tạo nội dung HTML email
        String subject = "KaizenStream - Banning Account notification";
        String content = getHtmlContent(streamerName, banReason, banDuration.toString());

        // Tạo payload cho email gửi tới Resend API
        String emailPayload = createEmailPayload(streamerEmail, content, subject);

        // Tạo headers yêu cầu
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + resendApiKey);  // API Key của Resend
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Tạo body request
        HttpEntity<String> entity = new HttpEntity<>(emailPayload, headers);

        try {
            // Gửi email qua Resend API
            ResponseEntity<String> response = restTemplate.exchange(
                    RESEND_API_URL, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("📧 Email sent successfully!");
            } else {
                System.err.println("Error sending email: " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("Error while sending email: " + e.getMessage());
            throw new AppException(ErrorCode.INVALID_EMAIL);  // Thêm thông báo lỗi cụ thể
        }
    }

    // Tạo payload email bằng Jackson
    private String createEmailPayload(String recipientEmail, String content, String subject) {
        try {
            // Tạo map với dữ liệu cần thiết
            Map<String, Object> emailData = new HashMap<>();
            emailData.put("from", "KaizenStream <onboarding@resend.dev>");
            emailData.put("to", new String[]{recipientEmail});
            emailData.put("subject", subject);
            emailData.put("html", content);

            // Chuyển map thành chuỗi JSON hợp lệ
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(emailData);
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_EMAIL);
        }
    }

    // Hàm tạo nội dung HTML email
    private String getHtmlContent(String username, String lockedAt, String reason) {
        String lockDuration = lockedAt;
        return """
        <!DOCTYPE html>
        <html lang="en">
          <head>
            <meta charset="UTF-8" />
            <title>Account Suspension Notification</title>
          </head>
          <body style="margin: 0; padding: 0; font-family: 'Segoe UI', sans-serif; background-color: #f5f7fa;">
            <table align="center" width="600" cellpadding="0" cellspacing="0"
              style="background-color: #ffffff; border-radius: 8px; overflow: hidden;
              box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); margin-top: 40px;">

              <tr style="background-color: #111827;">
                <td style="padding: 24px;">
                  <table cellpadding="0" cellspacing="0" width="100%%">
                    <tr>
                      <td style="vertical-align: middle; width: 60px; text-align: center;">
                        <img src="https://res.cloudinary.com/dpu7db88i/image/upload/v1744612916/v8fmrupdsepa3zqomxfa.png"
                             alt="KaizenStream Logo" width="50" height="50"
                             style="border-radius: 50%%; display: block; margin: 0 auto;" />
                      </td>
                      <td style="vertical-align: middle; padding-left: 16px;">
                        <div style="color: #ffffff;">
                          <h2 style="margin: 0; font-size: 20px;">KaizenStream</h2>
                          <p style="margin: 4px 0 0; font-size: 14px; color: #e0e7ff;">
                            Notification of account suspension
                          </p>
                        </div>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>

              <tr>
                <td style="padding: 24px;">
                  <p>Hello <strong>%s</strong>,</p>
                  <p>
                    We would like to inform you that your account on <strong>KaizenStream</strong> has been
                    <span style="color: #dc2626;"><strong>suspended</strong></span> as of <strong>%s</strong>.
                  </p>

                  <h3 style="color: #4f46e5; margin-top: 24px;">🔒 Suspension Details:</h3>
                  <ul style="padding-left: 20px; line-height: 1.6;">
                    <li><strong>Account:</strong> %s</li>
                    <li><strong>Suspension Time:</strong> %s</li>
                    <li><strong>Reason: Your livestream content violated platform policy</strong> %s</li>
                  </ul>

                  <p>
                    You can review our full platform policy here:<br />
                    🔗 <a href="https://kaizenstream.com/policy" style="color: #4f46e5;">
                    https://kaizenstream.com/policy</a>
                  </p>

                  <h3 style="color: #4f46e5; margin-top: 24px;">📩 Need Help?</h3>
                  <p>
                    If you believe this is a mistake or have any questions, please contact us at:
                  </p>
                  <ul style="padding-left: 20px; line-height: 1.6;">
                    <li><strong>Email:</strong> <a href="mailto:KaizenStream <onboarding@resend.dev>"
                    style="color: #4f46e5;">KaizenStream <onboarding@resend.dev></a></li>
                  </ul>

                  <p style="margin-top: 32px;">Sincerely,<br /><strong>KaizenStream Community Team</strong></p>
                </td>
              </tr>

              <tr style="background-color: #f3f4f6; text-align: center;">
                <td style="padding: 16px; font-size: 12px; color: #6b7280;">
                  © 2025 KaizenStream. All rights reserved.
                </td>
              </tr>
            </table>
          </body>
        </html>
        """.formatted(username, lockedAt, username, lockDuration, reason);
    }
}
