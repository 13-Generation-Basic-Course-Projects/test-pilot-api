package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.config.GoogleConfig;
import com.both.testing_pilot_backend.event.ForgetPasswordEvent;
import com.both.testing_pilot_backend.event.UserRegistrationEvent;
import com.both.testing_pilot_backend.exceptions.BadRequestException;
import com.both.testing_pilot_backend.exceptions.EmailAlreadyExistException;
import com.both.testing_pilot_backend.exceptions.NotFoundException;
import com.both.testing_pilot_backend.jwt.JwtService;
import com.both.testing_pilot_backend.model.OtpCode;
import com.both.testing_pilot_backend.model.User;
import com.both.testing_pilot_backend.model.UserAccount;
import com.both.testing_pilot_backend.dto.request.RegisterRequestDTO;
import com.both.testing_pilot_backend.dto.response.AuthResponse;
import com.both.testing_pilot_backend.dto.response.GithubUserResponse;
import com.both.testing_pilot_backend.repository.UserAccountRepository;
import com.both.testing_pilot_backend.repository.UserRepository;
import com.both.testing_pilot_backend.service.AuthService;
import com.both.testing_pilot_backend.service.OTPService;
import com.both.testing_pilot_backend.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final UserService userService;
    private final OTPService otpService;
    private final GoogleConfig googleConfig;
    private final UserAccountRepository userAccountRepository;
    private final JwtService jwtService;
    private final GithubService githubService;


    @Override
    public void register(RegisterRequestDTO requestDTO) {
        User user = userRepository.getUserByEmail(requestDTO.getEmail());

        if (user == null) {
            user = User.builder().name(requestDTO.getUsername()).email(requestDTO.getEmail()).isVerified(false).password(passwordEncoder.encode(requestDTO.getPassword())).build();
            User newUser = userRepository.saveUser(user);


            String plainOtp = otpService.generateAndPersistOtp(newUser);

            UserRegistrationEvent userRegistrationEvent = new UserRegistrationEvent(this, requestDTO.getEmail(), requestDTO.getUsername(), plainOtp);
            eventPublisher.publishEvent(userRegistrationEvent);
        } else {
            throw new EmailAlreadyExistException("Email already exist!");
        }
    }

    @Override
    public void resendEmailVerification(String email) {

        User user = userRepository.getUserByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        if (user != null && user.getIsVerified() == true) {
            throw new BadRequestException("Email has already been verified");
        }


        OtpCode otpCode = otpService.findByUserId(user.getUserId());
        if (otpCode != null) {
            otpService.deleteOtp(user.getUserId());
        }


        String plainOtp = otpService.generateAndPersistOtp(user);

        UserRegistrationEvent userRegistrationEvent = new UserRegistrationEvent(this, user.getEmail(), user.getUsername(), plainOtp);
        eventPublisher.publishEvent(userRegistrationEvent);
    }

    @Override
    public void verifyEmail(String email, String token) {
        User user = userRepository.getUserByEmail(email);

        if (user == null) {
            throw new NotFoundException("Verification failed: User account not found.");
        }

        if (user.getIsVerified() == true) {
            throw new BadRequestException("This account has already been verified.");
        }

        boolean isOtpValid = otpService.validateOTP(token, user.getUserId());

        if (isOtpValid) {
            userService.updateIsVerified(user.getUserId(), true);
        }
    }

    @Override
    public void requestForgetPassword(String email) {
        User user = userRepository.getUserByEmail(email);

        if (user == null) {
            throw new NotFoundException("User not found");
        }

        if (user.getIsVerified() == false) {
            throw new BadRequestException("Email has not been verified yet");
        }

        otpService.deleteOtp(user.getUserId());
        String plainOtp = otpService.generateAndPersistOtp(user);

        ForgetPasswordEvent forgetPasswordEvent = new ForgetPasswordEvent(this, user, plainOtp);
        eventPublisher.publishEvent(forgetPasswordEvent);
    }

    @Override
    public void resetPassword(String email, String plainOtp, String newPassword) {
        User user = userRepository.getUserByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }


        OtpCode otpCode = otpService.findByUserId(user.getUserId());
        if (otpCode == null || otpCode.isExpired()) {
            throw new BadRequestException("Invalid or expired OTP. Please request a new one.");
        }

        userService.updatePassword(user.getUserId(), passwordEncoder.encode(newPassword));
        otpService.deleteOtp(user.getUserId());
    }

    @Override
    public AuthResponse googleOauthCallback(String googleToken) throws GeneralSecurityException, IOException {

        // step 1: verify token
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleConfig.getClientId()))
                .build();

        GoogleIdToken idToken = verifier.verify(googleToken);
        if (idToken == null) {
            throw new BadRequestException("Invalid Google token.");
        }

        Payload payload = idToken.getPayload();

        // Step 2: Extract user information
        String email = payload.getEmail();
        String userName = (String) payload.get("name");
        String profileImage = (String) payload.get("picture");
        String providerId = payload.getSubject();
        String provider = "google";

        // Step 3: Find or create User
        User user = userRepository.getUserByEmail(email);
        if (user != null) {
            if (user.getPassword() != null) {
                throw new BadRequestException("This email is already registered. Please sign in using your email and password.");
            }
        } else  {
            user = User.builder()
                    .email(email)
                    .name(userName)
                    .profileImage(profileImage)
                    .isVerified(true)
                    .password(null)
                    .build();
            user = userRepository.saveUser(user);
        }

        // Step 4: Find or create UserAccount
        UserAccount userAccount = userAccountRepository.findByProviderNameAndProviderId(provider, providerId);
        if (userAccount == null) {
            UserAccount newUserAccount = UserAccount.builder().provider(provider).providerId(providerId).build();
            userAccountRepository.saveUserAccount(newUserAccount, user.getUserId());
        }

        // Step 5: Generate JWT token
        return generateAuthResponse(email);
    }

    @Override
    public AuthResponse gitOauthLogin(String githubCode) {
        GithubUserResponse githubUserResponseMono = githubService.getUserByAccessToken(githubCode).block();

        // extract user information
        String email = githubUserResponseMono.getEmail();
        String userName = githubUserResponseMono.getName();
        String provider = githubUserResponseMono.getProvider();
        String providerId = githubUserResponseMono.getProviderId();
        String profileImage = githubUserResponseMono.getProfileImage();

        // find and create user
        User user = userRepository.getUserByEmail(email);
        if(user != null) {
            if(user.getPassword() != null) {
                throw new BadRequestException("This email is already registered. Please sign in using your email and password.");
            }
        }else {
            user = User.builder()
                    .email(email)
                    .name(userName)
                    .profileImage(profileImage)
                    .isVerified(true)
                    .password(null)
                    .build();
            user = userRepository.saveUser(user);
        }

        // create user account
        UserAccount userAccount = userAccountRepository.findByProviderNameAndProviderId(provider, providerId);
        if (userAccount == null) {
            UserAccount newUserAccount = UserAccount.builder().provider(provider).providerId(providerId).build();
            userAccountRepository.saveUserAccount(newUserAccount, user.getUserId());
        }
        return  generateAuthResponse(email);
    }

    private AuthResponse generateAuthResponse(String email) {
        UserDetails userDetails = userService.loadUserByUsername(email);
        final String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token);
    }

    public UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new BadRequestException("Authentication is required");
        }
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        return userService.getUserByEmail(userDetails.getUsername()).getUserId();
    }
}
