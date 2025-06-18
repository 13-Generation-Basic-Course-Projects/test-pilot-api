package com.both.testing_pilot_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for user")
public class UserRequest {

    @NotBlank(message = "Collection name cannot be blank")
    @Size(min = 3, max = 255, message = "Collection name must be between 3 and 255 characters")
    @Pattern(regexp = "^[\\p{L}\\p{N} _'\"!?.,:()\\[\\]-]{3,255}$", message = "Collection name contains invalid characters")
    @Schema(description = "Name of the collection", example = "My First Collection")
    private String name;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must be within 255 characters")
    @Schema(description = "User's email address", example = "user@example.com")
    private String email;
}
