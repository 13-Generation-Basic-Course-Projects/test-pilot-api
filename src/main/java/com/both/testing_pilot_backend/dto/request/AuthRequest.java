package com.both.testing_pilot_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request for authenticating users")
public class AuthRequest {

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid email format")
	@Size(max = 255, message = "Email must be within 255 characters")
	@Schema(description = "User's email address", example = "user@example.com")
	private String email;

	@NotBlank(message = "Password cannot be blank")
	@Pattern(
					regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}:;\"'<>?,./]).{8,20}$",
					message = "Password must be 8-20 characters, include upper and lower case letters, a number, and a special character"
	)
	@Schema(
					description = "Password must include uppercase, lowercase, number, and special character",
					example = "Str0ngP@ssword!"
	)
	private String password;
}
