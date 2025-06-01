package com.both.testing_pilot_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProjectCollaboratorRequest {
    private UUID projectId;
    private String email;
}