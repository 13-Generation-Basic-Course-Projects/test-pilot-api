package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.exceptions.BadRequestException;
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


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.getUserByEmail(email);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }



    @Override
    public void updateIsVerified(UUID userId, boolean isVerified) {
        userRepository.updateIsVerified(userId, isVerified);
    }

    @Override
    public void updatePassword(UUID userId, String newPassword) {
        userRepository.updatePassword(userId, newPassword);
    }

    @Override
    public User updateUserInfo(UUID userId, String name, String email) {
        return userRepository.updateUserInfo(userId, name, email);
    }

    @Override
    public User getUserInfo(UUID currentUserId) {
        return userRepository.findById(currentUserId);
    }

    @Override
    public InputStream previewFileByFileName(String fileName){
        return fileService.getFileByFileName(fileName);
    }

    @Override
    public User uploadUserProfileImage(UUID currentUserId, MultipartFile file) {
        FileMetadata profileImageUpload = fileService.uploadFile(file);
        System.out.println("profileImageUpload" + profileImageUpload);
        String fileUrl = profileImageUpload.getFileUrl();

        return userRepository.uploadUserProfileImage(currentUserId, fileUrl);
    }
}
