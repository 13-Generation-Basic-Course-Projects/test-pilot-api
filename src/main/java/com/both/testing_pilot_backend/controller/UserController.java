package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.request.UserRequest;
import com.both.testing_pilot_backend.dto.response.CustomApiResponse;
import com.both.testing_pilot_backend.dto.response.UserDTO;
import com.both.testing_pilot_backend.model.User;
import com.both.testing_pilot_backend.model.mapper.UserMapper;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final UserMapper userMapper;

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
    public ResponseEntity<CustomApiResponse<UserDTO>> updateUserInfo(@Valid @RequestBody UserRequest request) {
        UUID currentUserId = authUtils.getUserDetails().getUserId();
        User updatedUserInfo = userService.updateUserInfo(currentUserId, request.getName(), request.getEmail());

        UserDTO userDTO = userMapper.toDTO(updatedUserInfo);

        CustomApiResponse<UserDTO> apiResponse = CustomApiResponse.<UserDTO>builder()
            .message("Updated user profile info successfully!")
            .status(HttpStatus.OK)
            .success(true)
            .payload(userDTO)
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
    public ResponseEntity<CustomApiResponse<UserDTO>> getUserInfo() {
        UUID currentUserId = authUtils.getUserDetails().getUserId();
        User userInfo = userService.getUserInfo(currentUserId);

        UserDTO userDTO = userMapper.toDTO(userInfo);

        CustomApiResponse<UserDTO> apiResponse = CustomApiResponse.<UserDTO>builder()
                .message("Retrieved user profile info successfully!")
                .status(HttpStatus.OK)
                .success(true)
                .payload(userDTO)
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
    public ResponseEntity<CustomApiResponse<UserDTO>> uploadProfileImage(@RequestParam("file-name") MultipartFile file) {
        UUID currentUserId = authUtils.getUserDetails().getUserId();
        User userProfileImageUpdate = userService.uploadUserProfileImage(currentUserId, file);

        UserDTO userDTO = userMapper.toDTO(userProfileImageUpdate);

        CustomApiResponse<UserDTO> apiResponse = CustomApiResponse.<UserDTO>builder()
            .message("Updated user profile image successfully!")
            .status(HttpStatus.OK)
            .success(true)
            .payload(userDTO)
            .timestamps(LocalDateTime.now())
            .build();

        return ResponseEntity.ok(apiResponse);
    }
}
