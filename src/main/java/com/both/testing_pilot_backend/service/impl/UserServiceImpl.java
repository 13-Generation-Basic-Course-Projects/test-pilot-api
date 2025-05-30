package com.both.testing_pilot_backend.service.impl;


import com.both.testing_pilot_backend.model.User;
import com.both.testing_pilot_backend.repository.UserRepository;
import com.both.testing_pilot_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
    public void updateUserInfo(UUID userId, String name, String email) {
        userRepository.updateUserInfo(userId, name, email);
    }

}
