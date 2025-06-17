package com.both.testing_pilot_backend.dto.mapper;

import com.both.testing_pilot_backend.dto.response.UserDTO;
import com.both.testing_pilot_backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
  UserDTO toDTO(User user);
}