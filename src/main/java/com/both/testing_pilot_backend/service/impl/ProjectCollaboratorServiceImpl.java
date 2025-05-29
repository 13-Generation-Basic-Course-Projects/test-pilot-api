package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.dto.request.ProjectCollaboratorRequest;
import com.both.testing_pilot_backend.event.InviteCollaboratorEvent;
import com.both.testing_pilot_backend.repository.ProjectCollaboratorRepository;
import com.both.testing_pilot_backend.repository.UserRepository;
import com.both.testing_pilot_backend.model.User;
import com.both.testing_pilot_backend.service.ProjectCollaboratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectCollaboratorServiceImpl implements ProjectCollaboratorService {

    private final ProjectCollaboratorRepository projectCollaboratorRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void inviteCollaborator(ProjectCollaboratorRequest request) {
        // 1. Find user by email
        User user = userRepository.getUserByEmail(request.getCollaboratorEmail());
        if (user == null) {
            throw new IllegalArgumentException("User with email not found");
        }

        // 2. Generate new UUID for project collaborator
        UUID projectCollaboratorId = UUID.randomUUID();

        // 3. Generate verification code (just for email use, not stored)
        String verificationCode = generateVerificationCode();

        // 4. Save collaborator (isVerify = false)
        projectCollaboratorRepository.addCollaborator(
                projectCollaboratorId,
                request.getProjectId(),
                user.getUserId(),
                false
        );

        // 5. Publish event with code
        eventPublisher.publishEvent(new InviteCollaboratorEvent(
                this,
                request.getCollaboratorEmail(),
                projectCollaboratorId,
                verificationCode // <-- Add this to your event class
        ));
    }

    @Override
    public void verifyCollaboratorInvite(UUID projectCollaboratorId) {

    }

    private String generateVerificationCode() {
        // Simple 6-digit code
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }

}
