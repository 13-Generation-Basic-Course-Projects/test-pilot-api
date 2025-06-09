package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.model.OtpCode;
import com.both.testing_pilot_backend.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

public interface OTPService {
    String generateAndPersistOtp(User user);

    boolean validateOTP(String plainOtp, UUID userId);

    OtpCode findByUserId(UUID userId);

    void deleteOtp(UUID userId);

    OtpCode updateOtp(OtpCode otpCode);
}
