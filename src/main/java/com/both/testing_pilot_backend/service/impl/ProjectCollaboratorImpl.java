package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.repository.ProjectCollaboratorRepository;
import com.both.testing_pilot_backend.service.ProjectCollaboratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
@RequiredArgsConstructor
public class ProjectCollaboratorImpl implements ProjectCollaboratorService {
    private final ProjectCollaboratorRepository projectCollaboratorRepository;
    @Override
    public UUID inviteUserToProject(UUID projectId, UUID userId) {
        UUID collaboratorId = UUID.randomUUID();
        projectCollaboratorRepository.addCollaborator(collaboratorId, projectId, userId);
        return collaboratorId;
    }

    @Override
    public boolean isProjectCollaborator(UUID projectId, UUID userId) {
        return projectCollaboratorRepository.isProjectCollaborator(projectId,userId) ;
    }

    @Override
    public void removeCollaborator(UUID projectId, UUID userId) {
        projectCollaboratorRepository.removeCollaborator(projectId, userId);
    }


}
