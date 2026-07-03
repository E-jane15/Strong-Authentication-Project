package com.example.authenticationsystem.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@Service
public class OtpService {

    private final SecureRandom random = new SecureRandom();

    private final Map<String, String> otpStorage = new HashMap<>();

    public String generateOtp(String email) {

        String otp = String.format("%06d",
                random.nextInt(1000000));

        otpStorage.put(email, otp);

        return otp;
    }

    public boolean verifyOtp(String email, String otp) {

        String savedOtp = otpStorage.get(email);

        if (savedOtp == null) {
            return false;
        }

        if (!savedOtp.equals(otp)) {
            return false;
        }

        otpStorage.remove(email);

        return true;
    }

}