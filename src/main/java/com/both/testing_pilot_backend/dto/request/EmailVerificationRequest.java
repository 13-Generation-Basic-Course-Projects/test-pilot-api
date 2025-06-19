package com.both.testing_pilot_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request for email verification with OTP")
public class EmailVerificationRequest {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email is invalid")
    @Size(max = 255, message = "Email must not be longer than 255 characters")
    @Schema(description = "User's email address", example = "user@example.com")
    private String email;

    @NotBlank(message = "OTP cannot be blank")
    @Size(min = 6, max = 6, message = "OTP must be exactly 6 characters")
    @Schema(description = "One-time password for verification", example = "123456")
    private String otp;
}
