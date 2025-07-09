package org.example.smartlawgt.integration.otp.services;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {
    private final Map<String, OtpData> otpStorage = new HashMap<>();

    public String generateOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(email, new OtpData(otp, LocalDateTime.now().plusMinutes(5)));
        return otp;
    }

    public boolean verifyOtp(String email, String inputOtp) {
        OtpData data = otpStorage.get(email);
        if (data == null || LocalDateTime.now().isAfter(data.expirationTime)) return false;
        boolean isValid = data.otp.equals(inputOtp);
        if (isValid) otpStorage.remove(email); // remove after success
        return isValid;
    }

    private record OtpData(String otp, LocalDateTime expirationTime) {}
}
