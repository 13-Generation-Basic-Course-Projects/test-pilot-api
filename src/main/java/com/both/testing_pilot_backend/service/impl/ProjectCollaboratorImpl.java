package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.repository.ProjectCollaboratorRepository;
import com.both.testing_pilot_backend.service.ProjectCollaboratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
@RequiredArgsConstructor
public class ProjectCollaboratorImpl implements ProjectCollaboratorService {
    private final ProjectCollaboratorRepository repository;
    @Override
    public void inviteUserToProject(UUID projectId, UUID userId) {
        UUID collaboratorId = UUID.randomUUID();
        repository.addCollaborator(collaboratorId, projectId, userId);
    }
}
