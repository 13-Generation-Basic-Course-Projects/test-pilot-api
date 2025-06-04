package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.request.*;
import com.both.testing_pilot_backend.jwt.JwtService;
import com.both.testing_pilot_backend.model.User;
import com.both.testing_pilot_backend.dto.response.AuthResponse;
import com.both.testing_pilot_backend.dto.response.CustomApiResponse;
import com.both.testing_pilot_backend.service.AuthService;
import com.both.testing_pilot_backend.service.UserService;
import com.both.testing_pilot_backend.service.impl.GithubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;

import org.springframework.security.access.AccessDeniedException;

@RestController
@RequestMapping("/api/v1/auths")
@Tag(name = "Authentication", description = "User authentication and authorization operations") // Enhanced Tag
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth") // Applies to all endpoints unless overridden
public class AuthController {
    private final UserService userService;
    private final AuthService authService;
    private final GithubService githubService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    // Helper method for authentication. Exceptions thrown here will be caught by GlobalExceptionHandler.
    private void authenticate(String email, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException e) {
            throw new AccessDeniedException("User account is disabled. Please contact support.", e);
        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("Incorrect email or password. Please try again.", e);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticates a user with email and password and returns a JWT token.", responses = {@ApiResponse(responseCode = "200", description = "User logged in successfully", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials or malformed request", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            // ProblemDetail for errors
            @ApiResponse(responseCode = "403", description = "Email not verified or account disabled", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Authentication request with user email and password", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthRequest.class)))
    public ResponseEntity<CustomApiResponse<AuthResponse>> authenticate(@RequestBody AuthRequest request) throws Exception {
        final User user = userService.getUserByEmail(request.getEmail());

        if (user.getIsVerified() == false) {
            throw new AccessDeniedException("Email has not been verified yet. Please verify your email and try again.");
        }

        authenticate(request.getEmail(), request.getPassword());
        final UserDetails userDetails = userService.loadUserByUsername(request.getEmail());
        final String token = jwtService.generateToken(userDetails);

        AuthResponse authResponse = new AuthResponse(token);

        CustomApiResponse<AuthResponse> apiResponse = CustomApiResponse.<AuthResponse>builder()
                .message("User logged in successfully.")
                .status(HttpStatus.OK)
                .success(true)
                .payload(authResponse)
                .timestamps(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/register")
    @Operation(summary = "Register New User", description = "Registers a new user account. An email verification link is sent upon successful registration.", responses = {@ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation errors (e.g., duplicate email, weak password)", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Registration request with user details", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterRequestDTO.class)))
    public ResponseEntity<CustomApiResponse<?>> register(@Valid @RequestBody RegisterRequestDTO request) {
        authService.register(request);

        CustomApiResponse<?> apiResponse = CustomApiResponse.builder()
                .message("User registered successfully. Please check your email to verify your account.")
                .status(HttpStatus.CREATED)
                .success(true)
                .timestamps(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/verification/resend")
    @Operation(summary = "Resend Email Verification", description = "Resends the email verification OTP to the specified email address.", responses = {@ApiResponse(responseCode = "200", description = "New verification link sent", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email format", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "User not found for the given email", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Email address to resend verification to", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmailVerificationResendRequest.class)))
    public ResponseEntity<CustomApiResponse<?>> resendEmailVerification(@RequestBody EmailVerificationResendRequest request) {
        authService.resendEmailVerification(request.getEmail());
        CustomApiResponse<?> apiResponse = CustomApiResponse.builder()
                .message("A new verification link has been sent to your email: " + request.getEmail())
                .status(HttpStatus.OK)
                .success(true)
                .timestamps(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/verification/verify")
    @Operation(summary = "Verify Email with OTP", description = "Verifies a user's email address using the provided OTP.", responses = {@ApiResponse(responseCode = "200", description = "Email successfully verified", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid OTP or email format", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Email and OTP for verification", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmailVerificationRequest.class)))
    public ResponseEntity<CustomApiResponse<?>> verifyEmail(@Valid @RequestBody EmailVerificationRequest request) {
        authService.verifyEmail(request.getEmail(), request.getOtp());
        CustomApiResponse<?> apiResponse = CustomApiResponse.builder()
                .message("Email has been successfully verified. You can now log in.")
                .status(HttpStatus.OK)
                .success(true)
                .timestamps(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/password/request-reset-otp")
    @Operation(summary = "Request Password Reset OTP", description = "Sends a password reset OTP to the user's email. Message is generic for security reasons.", responses = {@ApiResponse(responseCode = "200", description = "Password reset OTP sent (if account exists)", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email format", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Email address for password reset", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = ForgetPasswordRequest.class)))
    public ResponseEntity<CustomApiResponse<?>> sendForgetPassword(@Valid @RequestBody ForgetPasswordRequest request) {
        authService.requestForgetPassword(request.getEmail());
        CustomApiResponse<?> apiResponse = CustomApiResponse.builder()
                .message("If an account with that email exists, a password reset OTP has been sent.")
                .status(HttpStatus.OK)
                .success(true)
                .timestamps(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/password/reset-with-otp")
    @Operation(summary = "Reset Password with OTP", description = "Resets user's password using the provided email, OTP, and new password.", responses = {@ApiResponse(responseCode = "200", description = "Password successfully reset", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid OTP, email, or new password format", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Password reset request with email, OTP, and new password", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResetPasswordRequest.class)))
    public ResponseEntity<CustomApiResponse<?>> verifyForgetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
        CustomApiResponse<?> apiResponse = CustomApiResponse.builder()
                .message("Password has been successfully reset. You can now log in with your new password.")
                .status(HttpStatus.OK)
                .success(true)
                .timestamps(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/password/resend-reset-otp")
    @Operation(summary = "Resend Password Reset OTP", description = "Resends a password reset OTP to the user's email. Message is generic for security reasons.", responses = {@ApiResponse(responseCode = "200", description = "New password reset OTP sent (if account exists)", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email format", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Email address to resend password reset OTP to", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = ForgetPasswordRequest.class)))
    public ResponseEntity<CustomApiResponse<?>> resendResetPassword(@Valid @RequestBody ForgetPasswordRequest request) {
        authService.requestForgetPassword(request.getEmail());
        CustomApiResponse<?> apiResponse = CustomApiResponse.builder()
                .message("If an account with that email exists, a new password reset OTP has been sent.")
                .status(HttpStatus.OK)
                .success(true)
                .timestamps(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/google-login")
    @Operation(summary = "Google OAuth Login/Registration", description = "Handles Google OAuth callback to log in or register a user. Requires a Google ID token.", responses = {@ApiResponse(responseCode = "200", description = "Google login/registration successful", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Google token or authentication error", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public ResponseEntity<CustomApiResponse<AuthResponse>> googleLogin(@RequestParam @Schema(description = "Google ID token obtained from frontend OAuth flow", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") String googleToken) throws GeneralSecurityException, IOException {
        AuthResponse authResponse = authService.googleOauthCallback(googleToken);
        CustomApiResponse<AuthResponse> apiResponse = CustomApiResponse.<AuthResponse>builder()
                .message("Google login successful.")
                .status(HttpStatus.OK)
                .success(true)
                .timestamps(LocalDateTime.now())
                .payload(authResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("github-login")
    @Operation(summary = "GitHub OAuth Login/Registration", description = "Handles GitHub OAuth callback to log in or register a user. Requires a GitHub authorization code.", responses = {@ApiResponse(responseCode = "200", description = "GitHub login/registration successful", content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid GitHub code or authentication error", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public ResponseEntity<CustomApiResponse<AuthResponse>> githubLogin(@RequestParam @Schema(description = "GitHub authorization code obtained from frontend OAuth flow", example = "gho_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx") String githubCode) {
        AuthResponse authResponse = authService.gitOauthLogin(githubCode);

        CustomApiResponse<AuthResponse> apiResponse = CustomApiResponse.<AuthResponse>builder()
                .message("GitHub login successful.")
                .status(HttpStatus.OK)
                .success(true)
                .timestamps(LocalDateTime.now())
                .payload(authResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
