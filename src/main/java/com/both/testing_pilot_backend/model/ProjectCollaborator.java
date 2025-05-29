package com.both.testing_pilot_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCollaborator {
    private UUID projectCollaboratorId;
    private UUID projectId;
    private Boolean isVerify;

}
