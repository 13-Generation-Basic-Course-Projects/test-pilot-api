package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.dto.mapper.UserMapper;
import com.both.testing_pilot_backend.dto.response.UserDTO;
import com.both.testing_pilot_backend.exceptions.BadRequestException;
import com.both.testing_pilot_backend.exceptions.FileUploadException;
import com.both.testing_pilot_backend.exceptions.NotFoundException;
import com.both.testing_pilot_backend.model.FileMetadata;
import com.both.testing_pilot_backend.model.User;
import com.both.testing_pilot_backend.repository.UserRepository;
import com.both.testing_pilot_backend.service.FileService;
import com.both.testing_pilot_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FileService fileService;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserDetails userDetails = userRepository.getUserByEmail(email);
        if (userDetails == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return userDetails;
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.getUserByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found with email: " + email);
        }
        return userMapper.toDTO(user);
    }

    @Override
    public void updateIsVerified(UUID userId, boolean isVerified) {
        if (userId == null) {
            throw new BadRequestException("User ID must not be null.");
        }

        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException("User not found with ID: " + userId);
        }

        userRepository.updateIsVerified(userId, isVerified);
    }

    @Override
    public void updatePassword(UUID userId, String newPassword) {
        if (userId == null) {
            throw new BadRequestException("User ID must not be null.");
        }

        if (newPassword == null || newPassword.isEmpty()) {
            throw new BadRequestException("New password must not be empty.");
        }

        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException("User not found with ID: " + userId);
        }

        userRepository.updatePassword(userId, newPassword);
    }

    @Override
    public UserDTO updateUserInfo(UUID userId, String name, String email) {
        User updatedUserInfo = userRepository.updateUserInfo(userId, name, email);
        if (updatedUserInfo == null) {
            throw new NotFoundException("Unable to update. User not found with ID: " + userId);
        }
        return userMapper.toDTO(updatedUserInfo);
    }

    @Override
    public UserDTO getUserInfo(UUID currentUserId) {
        User userInfo = userRepository.findById(currentUserId);
        if (userInfo == null) {
            throw new NotFoundException("User not found with ID: " + currentUserId);
        }
        return userMapper.toDTO(userInfo);
    }

    @Override
    public InputStream previewFileByFileName(String fileName){
        InputStream file = fileService.getFileByFileName(fileName);
        if(file == null){
            throw new BadRequestException("Incorrect file name: " + fileName);
        }
        return file;
    }


    @Override
    public UserDTO uploadUserProfileImage(UUID currentUserId, MultipartFile file) {
        FileMetadata profileImageUpload;
        try {
            profileImageUpload = fileService.uploadFile(file);
        } catch (Exception e) {
            throw new FileUploadException("Failed to upload profile image");
        }

        String fileUrl = profileImageUpload.getFileUrl();
        User userProfileImage = userRepository.uploadUserProfileImage(currentUserId, fileUrl);
        if (userProfileImage == null) {
            throw new NotFoundException("User not found when updating profile image");
        }

        return userMapper.toDTO(userProfileImage);
    }
}
