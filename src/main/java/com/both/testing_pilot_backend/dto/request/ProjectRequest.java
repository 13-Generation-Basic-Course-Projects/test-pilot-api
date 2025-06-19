package com.both.testing_pilot_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {

    @NotNull
    @NotBlank
    @Size(min = 3, max = 255, message = "Project name must be between 3 to 255 characters")
    private String projectName;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String projectDescription;
}
