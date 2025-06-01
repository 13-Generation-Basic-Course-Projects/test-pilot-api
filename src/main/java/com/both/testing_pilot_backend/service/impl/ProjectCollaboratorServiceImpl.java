package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.dto.request.ProjectCollaboratorRequest;
import com.both.testing_pilot_backend.event.InviteCollaboratorEvent;
import com.both.testing_pilot_backend.exceptions.BadRequestException;
import com.both.testing_pilot_backend.exceptions.NotFoundException;
import com.both.testing_pilot_backend.model.ProjectCollaborator;
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

    public ProjectCollaborator inviteCollaborator(ProjectCollaboratorRequest request) {
        User existingUser = userRepository.getUserByEmail(request.getEmail());
        if (existingUser == null){
            throw new NotFoundException("User collaborator who is email " + request.getEmail() + "not found");
        }

        ProjectCollaborator newProjectCollaborator = projectCollaboratorRepository.savedCollaborator(request.getProjectId(), existingUser.getUserId());

        UUID projectCollaboratorId = newProjectCollaborator.getProjectCollaboratorId();
        if (projectCollaboratorId == null){
            throw new BadRequestException("Failed to retrieved project collaborator id after saving");
        }

        String acceptLink = "http://your-frontend.com/accept-invite?collaboratorId=" + projectCollaboratorId;

        eventPublisher.publishEvent(new InviteCollaboratorEvent(
                this,
                existingUser.getEmail(),
                projectCollaboratorId,
                acceptLink
        ));

        return newProjectCollaborator;
    }

    @Override
    public ProjectCollaborator acceptInviteLink(UUID id) {
        return projectCollaboratorRepository.acceptInviteLink(id);
    }
}

