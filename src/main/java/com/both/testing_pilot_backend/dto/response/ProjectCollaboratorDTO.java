package com.both.testing_pilot_backend.dto.response;

import com.both.testing_pilot_backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectCollaboratorDTO {
  private UUID projectCollaboratorId;
  private UUID projectId;
  private UUID userId;
  private UserDTO user;
}
