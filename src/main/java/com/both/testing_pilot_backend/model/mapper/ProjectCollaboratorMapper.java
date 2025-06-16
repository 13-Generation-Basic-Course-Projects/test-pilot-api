package com.both.testing_pilot_backend.model.mapper;

import com.both.testing_pilot_backend.dto.response.ProjectCollaboratorDTO;
import com.both.testing_pilot_backend.model.ProjectCollaborator;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserMapper.class})
public interface ProjectCollaboratorMapper {
  ProjectCollaboratorDTO toDTO(ProjectCollaborator projectCollaborator);

  List<ProjectCollaboratorDTO> toDTOList(List<ProjectCollaborator> collaborators);
}

