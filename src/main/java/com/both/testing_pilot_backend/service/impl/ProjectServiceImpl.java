package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.dto.mapper.ProjectMapper;
import com.both.testing_pilot_backend.dto.request.ProjectRequest;
import com.both.testing_pilot_backend.dto.request.PublicShareLinkItemRequest;
import com.both.testing_pilot_backend.dto.request.PublicShareLinkRequest;
import com.both.testing_pilot_backend.dto.response.ProjectDTO;
import com.both.testing_pilot_backend.exceptions.BadRequestException;
import com.both.testing_pilot_backend.exceptions.NotFoundException;
import com.both.testing_pilot_backend.jwt.JwtService;
import com.both.testing_pilot_backend.model.*;
import com.both.testing_pilot_backend.dto.request.PageRequest;
import com.both.testing_pilot_backend.dto.request.apiFeature.Filter;
import com.both.testing_pilot_backend.dto.request.apiFeature.Sort;
import com.both.testing_pilot_backend.repository.*;
import com.both.testing_pilot_backend.service.ProjectService;
import com.both.testing_pilot_backend.utils.AuthUtils;
import com.both.testing_pilot_backend.utils.SpecParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectMapper projectMapper;
    private final JwtService jwtService;
    private final AuthUtils authUtils;
    private final SpecParser parser;

    private final ProjectRepository projectRepository;
    private final CollectionRepository collectionRepository;
    private final RequestRepository requestRepository;
    private final PublicShareLinkRepository publicShareLinkRepository;
    private final PublicShareLinkItemRepository publicShareLinkItemRepository;

    @Override
    public boolean isProjectOwner(UUID projectId, UUID userId) {
        if (projectId == null || userId == null) {
            throw new BadRequestException("Project ID and User ID must not be null.");
        }

        return projectRepository.isProjectOwner(projectId, userId);
    }


    @Override
    public void deleteProject(UUID projectId) {
        if (projectId == null) {
            throw new BadRequestException("Project ID must not be null.");
        }

        Project existProject = projectRepository.findByProjectId(projectId);
        if (existProject == null) {
            throw new NotFoundException("Project with ID " + projectId + " does not exist.");
        }

        projectRepository.deleteByProjectId(projectId);
    }


    @Override
    public List<ProjectDTO> getAllProjects(MultiValueMap<String, String> params, PageRequest pageRequest, UUID currentUserId) {
        List<Filter> filters = parser.parseFilters(params);
        List<Sort> sorts = parser.parseSort(params.getFirst("sort"));
        List<Filter> search = parser.parseSearch(params);
        String cursor = params.getFirst("cursor");

        List<Project> projects = projectRepository.getAllProjects(filters, sorts,search, pageRequest, cursor, "projects", currentUserId);
        List<ProjectDTO> projectsDTO = projectMapper.toDTOList(projects);

        return projectsDTO;
    }

    @Override
    public ProjectDTO findByProjectId(UUID projectId) {
        Project project = projectRepository.findByProjectId(projectId);
        ProjectDTO projectDTO = projectMapper.toDTO(project);

        if(project == null) {
            throw new NotFoundException("Project not found");
        }

        return projectDTO;
    }

    @Override
    public ProjectDTO saveProject(ProjectRequest request) {
        Project project = Project.builder()
                .projectName(request.getProjectName().trim())
                .projectDescription(request.getProjectDescription().trim())
                .build();

        Project saveProject = projectRepository.saveProject(project, authUtils.getUserDetails().getUserId());
        ProjectDTO saveProjectDTO = projectMapper.toDTO(saveProject);

        return saveProjectDTO;
    }

    @Override
    public ProjectDTO updateProjectById(UUID projectId, ProjectRequest request) {
        Project existProject = projectRepository.findByProjectId(projectId);

        if(existProject == null) {
            throw new NotFoundException("Project not found");
        }
        existProject.setProjectName(request.getProjectName().trim());
        existProject.setProjectDescription(request.getProjectDescription().trim());
        existProject.setProjectId(projectId);

        Project updateProjectById = projectRepository.updateProjectById(existProject);
        ProjectDTO updateProjectByIdDTO = projectMapper.toDTO(updateProjectById);

        return updateProjectByIdDTO;
    }

    @Value("${app.dev.frontend.url}")
    private String appBaseUrl;

    @Override
    public String shareLinkByProjectId(UUID projectId) {
        UUID userSharedId = authUtils.getUserDetails().getUserId();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireAt = now.plusDays(7);

        String token = jwtService.generatePublicShareToken(userSharedId, expireAt);
        String verificationLink = String.format("%s/api/v1/publicShareLink/verify?token=%s", appBaseUrl, token);

        Project existProject = projectRepository.findByProjectId(projectId);
        if (existProject == null){
            throw new NotFoundException("Project id cannot be found.");
        }

        List<Collection> allCollections = collectionRepository.findByProjectId(existProject.getProjectId());
        if (allCollections == null || allCollections.isEmpty()) {
            throw new BadRequestException("This project does not contain any collections to share.");
        }

        for (Collection collection : allCollections){
            Collection existCollection =  collectionRepository.findById(collection.getId());
            List<Request> requests = requestRepository.findByCollectionId(existCollection.getId());

            PublicShareLinkRequest link = new PublicShareLinkRequest();
            link.setToken(token);
            link.setSharedItemType(existCollection.getName());
            link.setSharedItemId(existCollection.getId());
            link.setExpireAt(expireAt);

            PublicShareLink shareLink = publicShareLinkRepository.createPublicShareLink(link, authUtils.getUserDetails().getUserId());

            List<PublicShareLinkItemRequest> items = requests.stream()
                .map(req -> {
                    PublicShareLinkItemRequest item = new PublicShareLinkItemRequest();
                    item.setItemType(req.getName());
                    item.setItemId(req.getId());
                    item.setShareLinkId(shareLink.getShareLinkId());
                    publicShareLinkItemRepository.createPublicShareLinkItem(item);
                    return item;
                })
                .toList();
        }
        return verificationLink;
    }
}
