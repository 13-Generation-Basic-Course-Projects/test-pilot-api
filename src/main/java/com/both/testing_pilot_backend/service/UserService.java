package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.dto.response.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;


public interface UserService extends UserDetailsService {

    void updateIsVerified(UUID userId, boolean isVerified);

    void updatePassword(UUID userId, String newPassword);

    UserDTO getUserByEmail(String email);

    UserDTO updateUserInfo(UUID userId, String name, String email);

    UserDTO getUserInfo(UUID currentUserId);

    UserDTO uploadUserProfileImage(UUID currentUserId, MultipartFile file);

    InputStream previewFileByFileName(String fileName);
}
