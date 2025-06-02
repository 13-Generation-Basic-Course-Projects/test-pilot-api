package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.request.UserRequest;
import com.both.testing_pilot_backend.dto.response.CustomApiResponse;
import com.both.testing_pilot_backend.model.FileMetadata;
import com.both.testing_pilot_backend.model.User;
import com.both.testing_pilot_backend.service.FileService;
import com.both.testing_pilot_backend.service.UserService;
import com.both.testing_pilot_backend.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Profile Settings", description = "User authentication and authorization operations")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;
    private final AuthUtils authUtils;

    @PutMapping("/update/profile-info")
    @Operation(
        security = @SecurityRequirement(name = "bearerAuth"),
        summary = "Updated a user profile",
        description = "Updated a user profile",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully updated a user profile"),
            @ApiResponse(responseCode = "400", description = "Validation errors in request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
        }
    )
    public ResponseEntity<CustomApiResponse<User>> updateUserInfo(@Valid @RequestBody UserRequest request) {
        UUID currentUserId = authUtils.getUserDetails().getUserId();
        User updatedUserInfo = userService.updateUserInfo(currentUserId, request.getName(), request.getEmail());

        CustomApiResponse<User> apiResponse = CustomApiResponse.<User>builder()
            .message("Updated user profile info successfully!")
            .status(HttpStatus.OK)
            .success(true)
            .payload(updatedUserInfo)
            .timestamps(LocalDateTime.now())
            .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/profile-info")
    @Operation(
        security = @SecurityRequirement(name = "bearerAuth"),
        summary = "Retrieved a user profile info details",
        description = "Retrieved a user profile info details",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved a user profile"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
        }
    )
    public ResponseEntity<CustomApiResponse<User>> getUserInfo(){
        UUID currentUserId = authUtils.getUserDetails().getUserId();
        User userInfo = userService.getUserInfo(currentUserId);

        CustomApiResponse<User> apiResponse = CustomApiResponse.<User>builder()
            .message("Retrieved user profile info successfully!")
            .status(HttpStatus.OK)
            .success(true)
            .payload(userInfo)
            .timestamps(LocalDateTime.now())
            .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping(value = "/upload/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        security = @SecurityRequirement(name = "bearerAuth"),
        summary = "Updated a user profile image",
        description = "Updated a user profile image",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully updated a user profile image"),
            @ApiResponse(responseCode = "400", description = "Validation errors in path variable"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
        }
    )
    public ResponseEntity<CustomApiResponse<User>> uploadProfileImage(@RequestParam("file-name") MultipartFile file) {
        UUID currentUserId = authUtils.getUserDetails().getUserId();
        User userProfileImage = userService.uploadUserProfileImage(currentUserId, file);

        CustomApiResponse<User> apiResponse = CustomApiResponse.<User>builder()
            .message("Updated user profile image successfully!")
            .status(HttpStatus.OK)
            .success(true)
            .payload(userProfileImage)
            .timestamps(LocalDateTime.now())
            .build();

        return ResponseEntity.ok(apiResponse);
    }
}
