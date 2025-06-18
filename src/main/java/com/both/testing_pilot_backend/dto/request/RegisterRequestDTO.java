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
@Schema(description = "Request for register request")
public class RegisterRequestDTO {
    @Size(min = 3, max = 255, message = "Name must be between 3 to 255 characters")
    @NotBlank(message = "Name cannot be blank or whitespace")
    @Pattern(
            regexp = "^[A-Za-zÀ-ỹ\\s\\-']+$",
            message = "Name can only contain letters, spaces, hyphens, and apostrophes"
    )
    private String name;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Name must be within only 255 characters")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}:;\"'<>?,./]).{8,20}$",
            message = "Password must be 8-20 characters long and include upper and lower case letters, a number, and a special character"
    )
    private String password;
}
