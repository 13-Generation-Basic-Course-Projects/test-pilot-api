package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.request.UserRequest;
import com.both.testing_pilot_backend.service.UserService;
import com.both.testing_pilot_backend.utils.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthUtils authUtils;

    @PutMapping("/update-info")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateUserInfo(@Valid @RequestBody UserRequest request) {
        UUID currentUserId = authUtils.getCurrentUserId();
        userService.updateUserInfo(currentUserId, request.getName(), request.getEmail());
        return ResponseEntity.ok("User info updated successfully");
    }
}
