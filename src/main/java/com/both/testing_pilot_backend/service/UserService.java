package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


public interface UserService extends UserDetailsService {

    void updateIsVerified(UUID userId, boolean isVerified);

    void updatePassword(UUID userId, String newPassword);

    User getUserByEmail(String email);

    User updateUserInfo(UUID userId, String name, String email);

    User getUserInfo(UUID currentUserId);

    User uploadUserProfileImage(UUID currentUserId, MultipartFile file);

}
