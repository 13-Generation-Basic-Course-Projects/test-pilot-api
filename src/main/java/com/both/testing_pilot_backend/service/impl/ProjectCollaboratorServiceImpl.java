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
    private final com.both.testing_pilot_backend.util.VerificationCodeStorage verificationCodeStorage;

    @Override
    public void inviteCollaborator(ProjectCollaboratorRequest request) {
        // Find user by email
        User user = userRepository.getUserByEmail(request.getCollaboratorEmail());
        if (user == null) {
            throw new IllegalArgumentException("User with email not found");
        }

        // Generate new UUID for project collaborator
        UUID projectCollaboratorId = UUID.randomUUID();

        // Generate 6-digit verification code
        String verificationCode = String.format("%06d", (int)(Math.random() * 1_000_000));

        // Save collaborator (isVerify = false)
        projectCollaboratorRepository.addCollaborator(
                projectCollaboratorId,
                request.getProjectId(),
                user.getUserId(),
                false
        );

        // Store the verification code temporarily in memory
        verificationCodeStorage.storeCode(projectCollaboratorId, verificationCode);

        // Publish event to send email with the verification code
        eventPublisher.publishEvent(new InviteCollaboratorEvent(
                this,
                request.getCollaboratorEmail(),
                projectCollaboratorId,
                verificationCode
        ));
    }



    public void verifyCollaboratorInvite(UUID projectCollaboratorId, String code) {
        String storedCode = verificationCodeStorage.getCode(projectCollaboratorId);

        if (storedCode == null) {
            throw new IllegalArgumentException("Verification code expired or invalid");
        }

        if (!storedCode.equals(code)) {
            throw new IllegalArgumentException("Invalid verification code");
        }

        // Mark collaborator as verified in DB
        projectCollaboratorRepository.updateVerificationStatus(projectCollaboratorId);

        // Remove the code after successful verification
        verificationCodeStorage.removeCode(projectCollaboratorId);
    }

    @Override
    public void deleteCollaborator(UUID projectCollaboratorId) {
        projectCollaboratorRepository.deleteCollaborator(projectCollaboratorId);
    }
}

