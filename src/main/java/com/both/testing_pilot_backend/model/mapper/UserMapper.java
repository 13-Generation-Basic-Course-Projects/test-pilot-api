package com.both.testing_pilot_backend.model.mapper;

import com.both.testing_pilot_backend.model.User;
import com.both.testing_pilot_backend.model.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
  UserDTO toDTO(User user);
}