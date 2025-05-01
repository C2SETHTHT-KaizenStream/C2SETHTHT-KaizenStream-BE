package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.entity.Report;
import com.example.KaizenStream_BE.enums.ErrorCode;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.repository.ReportRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class EmailService {
    private JavaMailSender emailSender;
    ReportRepository reportRepository;

    // Gửi email với nội dung HTML
    public void sendHtmlEmail(String to, String subject, String htmlContent, String banReason, LocalDateTime banDuration) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String htmlContentWithCss = "<html>" +
                "<head>" +
                "<style>" +
                "    body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }" +
                "    .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 8px; }" +
                "    .header { text-align: center; padding: 10px; background-color: #ff4b5c; color: white; border-radius: 8px; }" +
                "    .header h1 { margin: 0; font-size: 24px; }" +
                "    .content { font-size: 16px; color: #333333; line-height: 1.5; margin-top: 20px; }" +
                "    .content p { margin: 0 0 15px 0; }" +
                "    .button { background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-size: 16px; margin-top: 20px; display: inline-block; }" +
                "    .footer { text-align: center; font-size: 12px; color: #777777; margin-top: 30px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "    <div class='header'>" +
                "        <h1>Community Standards Violation Notice</h1>" +
                "    </div>" +
                "    <div class='content'>" +
                "        <p>Dear User,</p>" +
                "        <p>We would like to inform you that your account has violated our Community Standards. Specifically, the violation occurred during one of your recent livestreams.</p>" +
                "        <p>We regret to inform you that your account has been temporarily suspended, and you will not be able to continue livestreaming until this issue is resolved.</p>" +
                "        <p>Please <a href='#' class='button'>View Details</a> to learn more about the violation and the steps you need to take next.</p>" +
                "        <p>We hope you will cooperate and adhere to our guidelines to ensure a safe and healthy environment for everyone.</p>" +
                "        <p>Thank you for your understanding and cooperation.</p>" +
                "    </div>" +
                "    <div class='footer'>" +
                "        <p>The KaizenStream Community Team</p>" +
                "        <p><small>Company ABC, 123 Street X, HCM City</small></p>" +
                "    </div>" +
                "</div>" +
                "</body>" +
                "</html>";
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContentWithCss, true);  // true để gửi email dạng HTML
        helper.setFrom("viethuy03.tech@gmail.com");  // Địa chỉ email của bạn

        emailSender.send(message);
    }

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
                    <li><strong>Email:</strong> <a href="mailto:viethuy03.tech@gmail.com" 
                    style="color: #4f46e5;">viethuy03.tech@gmail.com</a></li>
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

    @Async
    public void sendHtmlEmail(String reportId, String banReason, LocalDateTime banDuration) throws MessagingException {
        Report report = reportRepository.findById(reportId).orElseThrow(()-> new AppException(ErrorCode.REPORT_NOT_EXIST));

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        String subject = "KaizenStream - Banning Account notification";
        String content = getHtmlContent(report.getStream().getUser().getUserName(), banReason,banReason);
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(report.getStream().getUser().getEmail());

//            helper.setTo("huaphuminhhieu@gmail.com");
            helper.setSubject(subject);
            helper.setText(content, true); // true: gửi HTML
            helper.setFrom("viethuy03.tech@gmail.com");
            long start = System.currentTimeMillis();
            emailSender.send(message); // gửi qua SendGrid SMTP hoặc API
            long end = System.currentTimeMillis();
            System.out.print("📧 Email sent in {} ms" + (end - start));
        } catch (MessagingException e) {
            throw new AppException(ErrorCode.INVALID_EMAIL);
        }
    }
}
