package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.dto.request.ProjectCollaboratorRequest;
import com.both.testing_pilot_backend.event.InviteCollaboratorEvent;
import com.both.testing_pilot_backend.exceptions.BadRequestException;
import com.both.testing_pilot_backend.exceptions.DuplicateRecord;
import com.both.testing_pilot_backend.exceptions.NotFoundException;
import com.both.testing_pilot_backend.jwt.JwtService;
import com.both.testing_pilot_backend.model.Project;
import com.both.testing_pilot_backend.repository.ProjectCollaboratorRepository;
import com.both.testing_pilot_backend.repository.ProjectRepository;
import com.both.testing_pilot_backend.repository.UserRepository;
import com.both.testing_pilot_backend.model.User;
import com.both.testing_pilot_backend.model.ProjectCollaborator;
import com.both.testing_pilot_backend.service.ProjectCollaboratorService;
import com.both.testing_pilot_backend.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectCollaboratorServiceImpl implements ProjectCollaboratorService {

    private final ProjectCollaboratorRepository projectCollaboratorRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AuthUtils authUtils;
    private final JwtService jwtService;

    @Value("${app.dev.frontend.url}")
    private String appBaseUrl;

    @Override
    @Transactional
    public void inviteCollaborator(ProjectCollaboratorRequest request) {
        Project project = projectRepository.findByProjectId(request.getProjectId());
        if (project == null) {
            throw new NotFoundException("Project with ID '" + request.getProjectId() + "' not found.");
        }

        UUID currentUserId = authUtils.getUserDetails().getUserId();
        User currentUser = userRepository.findById(currentUserId);
        if (currentUser == null) {
            throw new AccessDeniedException("Authenticated user not found.");
        }
        if (!projectRepository.isProjectOwner(request.getProjectId(), currentUserId)) {
            throw new AccessDeniedException("You are not authorized to invite collaborators to this project. Only the project owner can invite.");
        }

        User invitedUser = userRepository.getUserByEmail(request.getCollaboratorEmail());
        if (invitedUser == null) {
            throw new NotFoundException("User with email '" + request.getCollaboratorEmail() + "' not found.");
        }

        if (invitedUser.getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException("You cannot invite yourself as a collaborator.");
        }

        if (project.getProjectOwner().getUserId().equals(invitedUser.getUserId())) {
            throw new DuplicateRecord("User '" + request.getCollaboratorEmail() + "' is already the owner of this project.");
        }
        if (projectCollaboratorRepository.isProjectCollaborator(request.getProjectId(), invitedUser.getUserId())) {
            throw new DuplicateRecord("User '" + request.getCollaboratorEmail() + "' is already a verified collaborator on this project.");
        }
        if (projectCollaboratorRepository.isUnverifiedCollaborator(request.getProjectId(), invitedUser.getUserId())) {
            throw new DuplicateRecord("User '" + request.getCollaboratorEmail() + "' has already been invited and is awaiting verification for this project. Please wait or contact the project owner.");
        }

        UUID projectCollaboratorId = UUID.randomUUID();
        String verificationToken = jwtService.generateInvitationToken(projectCollaboratorId, invitedUser.getUserId(), request.getProjectId());

        ProjectCollaborator newCollaboratorLink = ProjectCollaborator.builder()
                .projectCollaboratorId(projectCollaboratorId)
                .projectId(request.getProjectId())
                .userId(invitedUser.getUserId())
                .isVerify(false)
                .verificationToken(verificationToken)
                .build();
        try {
            projectCollaboratorRepository.save(newCollaboratorLink);
        } catch (DuplicateKeyException e) {
            throw new DuplicateRecord("Failed to invite collaborator: User '" + request.getCollaboratorEmail() + "' is already a collaborator on this project (DB conflict).");
        }

        String verificationLink = String.format("%s/collaborators/verify?token=%s", appBaseUrl, verificationToken);

        eventPublisher.publishEvent(new InviteCollaboratorEvent(
                this,
                request.getCollaboratorEmail(),
                projectCollaboratorId,
                verificationToken,
                verificationLink,
                project.getProjectName(),
                currentUser.getName(),
                invitedUser.getName()
        ));
    }


    @Override
    @Transactional
    public void verifyCollaboratorInvite(String verificationToken) {
        Claims claims;
        try {
            claims = jwtService.extractAllClaim(verificationToken);
        } catch (ExpiredJwtException e) {
            throw new AccessDeniedException("Invitation link has expired. Please request a new invitation.", e);
        } catch (MalformedJwtException | SignatureException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new BadRequestException("Invalid invitation token.");
        }

        System.out.println("Working after destrcuture jwt");
        UUID jwtPcId = UUID.fromString(claims.get("pcId", String.class));
        UUID jwtInvitedUserId = UUID.fromString(claims.get("uId", String.class));
        UUID jwtProjectId = UUID.fromString(claims.get("pId", String.class));

        System.out.println("Working in here before finding collaborator ");
        ProjectCollaborator collaboratorLink = projectCollaboratorRepository.findById(jwtPcId);
        if (collaboratorLink == null || collaboratorLink.getIsVerify()) {
            throw new NotFoundException("Verification link is invalid, or has already been used.");
        }

        UUID currentUserId = authUtils.getUserDetails().getUserId();
        if (!jwtInvitedUserId.equals(currentUserId)) {
            throw new AccessDeniedException("You are not authorized to verify this invitation. This invitation is for a different user.");
        }

        if (!collaboratorLink.getUserId().equals(jwtInvitedUserId) ||
                !collaboratorLink.getProjectId().equals(jwtProjectId) ||
                !collaboratorLink.getVerificationToken().equals(verificationToken)) { // Verify token matches stored token
            throw new BadRequestException("Invitation token claims do not match stored invitation details.");
        }

        projectCollaboratorRepository.updateVerificationStatus(collaboratorLink.getProjectCollaboratorId());
    }

    @Override
    @Transactional
    public void deleteCollaborator(UUID projectCollaboratorId) {
        ProjectCollaborator collaboratorLink = projectCollaboratorRepository.findById(projectCollaboratorId);
        if (collaboratorLink == null) {
            throw new NotFoundException("Collaborator link not found with ID: " + projectCollaboratorId);
        }

        UUID currentUserId = authUtils.getUserDetails().getUserId();
        if (!projectRepository.isProjectOwner(collaboratorLink.getProjectId(), currentUserId)) {
            throw new AccessDeniedException("You are not authorized to remove collaborators from this project. Only the project owner can delete collaborators.");
        }

        projectCollaboratorRepository.deleteById(projectCollaboratorId);
    }

    @Override
    public List<User> getCollaboratorByProjectId(UUID projectId) {
        Project project = projectRepository.findByProjectId(projectId);

        if(project == null) {
            throw new NotFoundException("Project not found");
        }

        List<ProjectCollaborator> projectCollaborators = projectCollaboratorRepository.findByProjectId(project.getProjectId());

        if(projectCollaborators.size() == 0) {
            throw new NotFoundException("Collaborator is empty");
        }

        List<User> users = projectCollaborators.stream().map(projectCollaborator -> projectCollaborator.getUser()).collect(
                Collectors.toList());

        return users;
    }
}
