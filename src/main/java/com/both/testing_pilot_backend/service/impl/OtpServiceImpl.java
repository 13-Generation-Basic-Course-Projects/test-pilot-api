package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.exceptions.BadRequestException;
import com.both.testing_pilot_backend.exceptions.NotFoundException;
import com.both.testing_pilot_backend.model.OtpCode;
import com.both.testing_pilot_backend.model.User;
import com.both.testing_pilot_backend.repository.OtpRepository;
import com.both.testing_pilot_backend.service.OTPService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OTPService {
    private final PasswordEncoder passwordEncoder;
    private final OtpRepository otpRepository;

    private String generatePlainOtp() {
        SecureRandom random = new SecureRandom();
        int otpInt = random.nextInt(900000) + 100000;
        return new DecimalFormat("000000").format(otpInt);
    }

    private String generateOTP(String plainTextOtp) {
        return passwordEncoder.encode(plainTextOtp);
    }

    @Override
    public String generateAndPersistOtp(User user) {
        String plainOtp = generatePlainOtp();
        String hashedOtp = generateOTP(plainOtp);

        OtpCode otpCode = new OtpCode(user, hashedOtp);
        otpRepository.saveOtp(otpCode, user.getUserId());

        return plainOtp;
    }

    @Override
    public boolean validateOTP(String otp, UUID userId) {
        OtpCode otpCode = otpRepository.findByUserId(userId);

        if (otpCode == null) {
            throw new NotFoundException("There is no record found");
        }

        if (passwordEncoder.matches(otp, otpCode.getHashOtp()) && !otpCode.isExpired()) {
            otpRepository.deleteHashOtp(userId);
        } else {
            throw new BadRequestException("Invalid or expired OTP. Please request a new one.");
        }

        return true;
    }

    @Override
    public OtpCode findByUserId(UUID userId) {
        return otpRepository.findByUserId(userId);
    }

    @Override
    public void deleteOtp(UUID userId) {
        otpRepository.deleteHashOtp(userId);
    }

    @Override
    public OtpCode updateOtp(OtpCode otpCode) {
        return otpRepository.updateOtp(otpCode);
    }
}
