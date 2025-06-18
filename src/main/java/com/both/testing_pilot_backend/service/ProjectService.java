package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.dto.request.ProjectRequest;
import com.both.testing_pilot_backend.dto.response.ProjectDTO;
import com.both.testing_pilot_backend.model.Project;
import com.both.testing_pilot_backend.dto.request.PageRequest;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.UUID;

public interface ProjectService {
    String shareLinkByProjectId(UUID projectId);

    ProjectDTO saveProject(ProjectRequest project);

    boolean isProjectOwner(UUID projectId, UUID userId);

    void deleteProject(UUID projectId);

    List<ProjectDTO> getAllProjects(MultiValueMap<String, String> params, PageRequest pageRequest, UUID currentUserId);

    ProjectDTO findByProjectId(UUID projectId);

    ProjectDTO updateProjectById(UUID projectId, ProjectRequest request);
}
