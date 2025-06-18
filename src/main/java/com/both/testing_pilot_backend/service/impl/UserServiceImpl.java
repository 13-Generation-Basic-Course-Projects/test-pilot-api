package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.dto.mapper.UserMapper;
import com.both.testing_pilot_backend.dto.response.UserDTO;
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
        return userRepository.getUserByEmail(email);
    }

    @Override
    public UserDTO getUserByEmail(String email) {

        User user = userRepository.getUserByEmail(email);

        UserDTO userDTO = userMapper.toDTO(user);

        return userDTO;
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
    public UserDTO updateUserInfo(UUID userId, String name, String email) {

        User updatedUserInfo = userRepository.updateUserInfo(userId, name, email);

        UserDTO updatedUserInfoDTO = userMapper.toDTO(updatedUserInfo);

        return updatedUserInfoDTO;
    }

    @Override
    public UserDTO getUserInfo(UUID currentUserId) {

        User userInfo = userRepository.findById(currentUserId);

        UserDTO userInfoDTO = userMapper.toDTO(userInfo);

        return userInfoDTO;
    }

    @Override
    public InputStream previewFileByFileName(String fileName){
        return fileService.getFileByFileName(fileName);
    }

    @Override
    public UserDTO uploadUserProfileImage(UUID currentUserId, MultipartFile file) {
        FileMetadata profileImageUpload = fileService.uploadFile(file);
        System.out.println("profileImageUpload" + profileImageUpload);
        String fileUrl = profileImageUpload.getFileUrl();

        User userProfileImage = userRepository.uploadUserProfileImage(currentUserId, fileUrl);

        UserDTO userProfileImageDTO = userMapper.toDTO(userProfileImage);

        return userProfileImageDTO;
    }
}
