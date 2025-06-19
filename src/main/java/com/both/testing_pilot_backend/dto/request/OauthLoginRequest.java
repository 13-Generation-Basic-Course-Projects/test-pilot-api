package com.both.testing_pilot_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for OAuth login request")
public class OauthLoginRequest {

    @NotBlank(message = "OAuth client ID cannot be blank")
    @Schema(description = "OAuth client identifier", example = "google", required = true)
    private String oauthClientId;
}
