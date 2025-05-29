package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.event.CollaboratorInvitedEvent;
import com.both.testing_pilot_backend.repository.ProjectCollaboratorRepository;
import com.both.testing_pilot_backend.service.ProjectCollaboratorService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProjectCollaboratorServiceImpl implements ProjectCollaboratorService {

    private final ProjectCollaboratorRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public ProjectCollaboratorServiceImpl(ProjectCollaboratorRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public UUID inviteUserToProject(UUID projectId, UUID inviterUserId, String collaboratorEmail) {
        // Create a new UUID for the invitation record
        UUID projectCollaboratorId = UUID.randomUUID();

        // Save the invitation to your database (implement this method as needed)
        repository.addCollaborator(projectCollaboratorId, projectId, collaboratorEmail, inviterUserId, false);

        // Generate invite code — here we just use the UUID string (you can customize)
        String inviteCode = projectCollaboratorId.toString();

        // Retrieve or assign a project name for the event (use your actual method)
        String projectName = "Project-" + projectId.toString(); // Simplify or fetch real project name from DB

        // Publish the collaborator invitation event to send email notification
        CollaboratorInvitedEvent event = new CollaboratorInvitedEvent(
                this,
                collaboratorEmail,
                projectName,
                inviteCode,
                inviterUserId.toString()
        );
        eventPublisher.publishEvent(event);

        // Return the invitation record ID
        return projectCollaboratorId;
    }
}
