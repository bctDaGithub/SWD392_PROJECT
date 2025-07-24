package org.example.smartlawgt.integration.otp.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@smartlaw.com}")
    private String fromEmail;

    public void sendOtp(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "SmartLaw GT");
            helper.setTo(toEmail);
            helper.setSubject("Mã OTP xác thực - SmartLaw GT");

            String htmlContent = generateOtpEmailTemplate(otp);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    private String generateOtpEmailTemplate(String otp) {
        return String.format("""
        <!DOCTYPE html>
        <html lang="vi">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Mã OTP - SmartLaw GT</title>
            <style>
                body {
                    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                    line-height: 1.6;
                    color: %s;
                    background-color: %s;
                    margin: 0;
                    padding: 20px;
                }
                .container {
                    max-width: 600px;
                    margin: 0 auto;
                    background-color: %s;
                    border-radius: 10px;
                    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
                    overflow: hidden;
                }
                .header {
                    background: linear-gradient(135deg, %s 0%%, %s 100%%);
                    color: white;
                    text-align: center;
                    padding: 30px 20px;
                }
                .header h1 {
                    margin: 0;
                    font-size: 28px;
                    font-weight: 600;
                }
                .header p {
                    margin: 10px 0 0 0;
                    font-size: 16px;
                    opacity: 0.9;
                }
                .content {
                    padding: 40px 30px;
                    text-align: center;
                }
                .otp-section {
                    background-color: %s;
                    border: 2px dashed %s;
                    border-radius: 15px;
                    padding: 30px;
                    margin: 30px 0;
                }
                .otp-label {
                    font-size: 16px;
                    color: %s;
                    margin-bottom: 15px;
                    font-weight: 500;
                }
                .otp-code {
                    font-size: 42px;
                    font-weight: bold;
                    color: %s;
                    letter-spacing: 10px;
                    margin: 20px 0;
                    font-family: 'Courier New', monospace;
                    text-shadow: 2px 2px 4px rgba(0,0,0,0.1);
                }
                .timer {
                    font-size: 14px;
                    color: %s;
                    margin-top: 15px;
                    font-weight: 600;
                }
                .warning {
                    background-color: %s;
                    border-left: 4px solid %s;
                    padding: 20px;
                    margin: 25px 0;
                    border-radius: 5px;
                    text-align: left;
                }
                .warning-title {
                    color: %s;
                    font-size: 16px;
                    font-weight: bold;
                    margin-bottom: 10px;
                }
                .warning-content {
                    font-size: 14px;
                    color: %s;
                    line-height: 1.5;
                }
                .instructions {
                    text-align: left;
                    margin: 30px 0;
                    padding: 25px;
                    background-color: %s;
                    border-radius: 8px;
                    border-left: 4px solid %s;
                }
                .instructions h3 {
                    color: %s;
                    margin-top: 0;
                    font-size: 18px;
                    margin-bottom: 15px;
                }
                .instructions ol {
                    padding-left: 20px;
                    color: %s;
                }
                .instructions li {
                    margin-bottom: 10px;
                    font-size: 14px;
                }
                .support {
                    background-color: %s;
                    padding: 20px;
                    border-radius: 8px;
                    margin-top: 25px;
                }
                .support h4 {
                    color: %s;
                    margin-top: 0;
                    margin-bottom: 10px;
                }
                .footer {
                    background-color: %s;
                    color: %s;
                    text-align: center;
                    padding: 25px;
                    font-size: 14px;
                }
                .footer a {
                    color: %s;
                    text-decoration: none;
                }
                @media (max-width: 600px) {
                    body { padding: 10px; }
                    .content { padding: 20px 15px; }
                    .otp-code { font-size: 32px; letter-spacing: 6px; }
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>🔐 SmartLaw GT</h1>
                    <p>Hệ thống tư vấn pháp lý thông minh</p>
                </div>

                <div class="content">
                    <h2 style="color: %s; margin-bottom: 20px;">Xác thực tài khoản</h2>
                    <p style="font-size: 16px; color: %s; margin-bottom: 25px;">
                        Chào bạn! Chúng tôi đã nhận được yêu cầu xác thực cho tài khoản của bạn.
                        Vui lòng sử dụng mã OTP bên dưới để hoàn tất quá trình đăng nhập.
                    </p>

                    <div class="otp-section">
                        <div class="otp-label">🔑 Mã OTP của bạn:</div>
                        <div class="otp-code">%s</div>
                        <div class="timer">⏰ Có hiệu lực trong 5 phút</div>
                    </div>

                    <div class="warning">
                        <div class="warning-title">⚠️ Lưu ý bảo mật</div>
                        <div class="warning-content">
                            • Không chia sẻ mã OTP này với bất kỳ ai<br>
                            • Mã chỉ có thể sử dụng một lần<br>
                            • Nếu bạn không thực hiện yêu cầu này, hãy bỏ qua email này
                        </div>
                    </div>

                    <div class="instructions">
                        <h3>📋 Cách sử dụng mã OTP:</h3>
                        <ol>
                            <li>Sao chép mã OTP <strong>%s</strong> ở trên</li>
                            <li>Quay lại trang web SmartLaw GT</li>
                            <li>Dán mã vào ô "Nhập mã OTP"</li>
                            <li>Nhấn nút "Xác thực" để hoàn tất</li>
                        </ol>
                    </div>

                    <div class="support">
                        <h4>🆘 Cần hỗ trợ?</h4>
                        <p style="margin: 0; color: %s; font-size: 14px;">
                            📧 Email: support@smartlaw.com<br>
                            📞 Hotline: 1900 1234 (8:00 - 22:00)
                        </p>
                    </div>
                </div>

                <div class="footer">
                    <p style="margin: 0 0 10px 0;">
                        <strong>SmartLaw GT</strong> - Hệ thống tư vấn pháp lý thông minh
                    </p>
                    <p style="margin: 0; font-size: 12px; color: %s;">
                        © 2024 SmartLaw GT. Mọi quyền được bảo lưu.<br>
                        🔒 Email này được gửi tự động, vui lòng không trả lời.
                    </p>
                </div>
            </div>
        </body>
        </html>
        """,
                "#333",           // body color
                "#f5f5f5",        // body background
                "#ffffff",        // container background
                "#667eea",        // header gradient start
                "#764ba2",        // header gradient end
                "#f8f9fa",        // otp-section background
                "#667eea",        // otp-section border
                "#666",           // otp-label color
                "#667eea",        // otp-code color
                "#e74c3c",        // timer color
                "#fff3cd",        // warning background
                "#ffc107",        // warning border
                "#856404",        // warning-title color
                "#856404",        // warning-content color
                "#f8f9fa",        // instructions background
                "#28a745",        // instructions border
                "#28a745",        // instructions h3 color
                "#555",           // instructions ol color
                "#e9ecef",        // support background
                "#495057",        // support h4 color
                "#343a40",        // footer background
                "#ffffff",        // footer color
                "#6c757d",        // footer a color
                "#333",           // h2 color
                "#666",           // p color
                otp,              // OTP code first occurrence
                otp,              // OTP code second occurrence
                "#495057",        // support p color
                "#6c757d"         // footer p color
        );
    }
}