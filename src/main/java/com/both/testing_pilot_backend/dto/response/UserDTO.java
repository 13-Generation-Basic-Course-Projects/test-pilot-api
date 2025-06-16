package com.both.testing_pilot_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
  private UUID userId;
  private String name;
  private String email;
  private Boolean isVerified;
  private String profileImage;
}
