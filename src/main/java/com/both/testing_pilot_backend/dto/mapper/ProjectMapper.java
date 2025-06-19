package com.both.testing_pilot_backend.dto.mapper;

import com.both.testing_pilot_backend.dto.response.ProjectDTO;
import com.both.testing_pilot_backend.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserMapper.class})
public interface ProjectMapper {
  ProjectDTO toDTO(Project project);

  List<ProjectDTO> toDTOList(List<Project> projects);
}
