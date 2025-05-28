package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.dto.request.ProjectRequest;
import com.both.testing_pilot_backend.exceptions.NotFoundException;
import com.both.testing_pilot_backend.model.Project;
import com.both.testing_pilot_backend.dto.request.PageRequest;
import com.both.testing_pilot_backend.dto.request.apiFeature.Filter;
import com.both.testing_pilot_backend.dto.request.apiFeature.Sort;
import com.both.testing_pilot_backend.repository.ProjectRepository;
import com.both.testing_pilot_backend.service.ProjectService;
import com.both.testing_pilot_backend.utils.AuthUtils;
import com.both.testing_pilot_backend.utils.SpecParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final AuthUtils authUtils;
    private final SpecParser parser;
    private final ProjectRepository projectRepository;

    @Override
    public boolean isProjectOwner(UUID projectId, UUID userId) {
        return projectRepository.isProjectOwner(projectId, userId);
    }

    @Override
    public void deleteProject(UUID projectId) {
        projectRepository.deleteByProjectId(projectId);
    }

    @Override
    public List<Project> getAllProjects(MultiValueMap<String, String> params, PageRequest pageRequest, UUID currentUserId) {
        List<Filter> filters = parser.parseFilters(params);
        List<Sort> sorts = parser.parseSort(params.getFirst("sort"));
        List<Filter> search = parser.parseSearch(params);
        String cursor = params.getFirst("cursor");

        return projectRepository.getAllProjects(filters, sorts,search, pageRequest, cursor, "projects", currentUserId);
    }

    @Override
    public Project findByProjectId(UUID projectId) {
        Project project = projectRepository.findByProjectId(projectId);

        if(project == null) {
            throw new NotFoundException("Project not found");
        }

        return project;
    }

    @Override
    public Project saveProject(ProjectRequest request) {
        Project project = Project.builder().projectName(request.getProjectName()).projectDescription(request.getProjectDescription()).build();
        return projectRepository.saveProject(project, authUtils.getUserDetails().getUserId());
    }

    @Override
    public Project updateProjectById(UUID projectId, ProjectRequest request) {
        Project project = projectRepository.findByProjectId(projectId);

        if(project == null) {
            throw new NotFoundException("Project not found");
        }
        project.setProjectName(request.getProjectName());
        project.setProjectDescription(request.getProjectDescription());
        project.setProjectId(projectId);

        Project updatedProject = projectRepository.updateProjectById(project);
        return updatedProject;
    }
}
