package com.both.testing_pilot_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for creating or updating a project")
public class ProjectRequest {

    @NotBlank(message = "Project name cannot be blank")
    @Size(min = 3, max = 255, message = "Project name must be between 3 to 255 characters")
    @Schema(description = "Name of the project", example = "My Awesome Project")
    private String projectName;

    @Size(max = 500, message = "Project description cannot exceed 500 characters.")
    @Schema(description = "Optional description of the project", example = "This project aims to ...", nullable = true)
    private String projectDescription;
}
