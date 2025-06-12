package com.both.testing_pilot_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCollaborator {
    private UUID projectCollaboratorId;
    private UUID projectId;
    private UUID userId;
    private User user;
    private Boolean isVerify;
    private String verificationToken;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
