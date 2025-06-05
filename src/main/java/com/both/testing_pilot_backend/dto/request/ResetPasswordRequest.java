package com.both.testing_pilot_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest{
    @NotBlank( message = "Email cannot be blank")
    @Email(message = "Email is invalid")
    @Size(max = 255, message = "Email must not be longer that 255 characters")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}:;\"'<>?,./]).{8,20}$",
            message = "Password must be 8-20 characters long and include upper and lower case letters, a number, and a special character"
    )
    private String newPassword;

    private String confirmPassword;
}
