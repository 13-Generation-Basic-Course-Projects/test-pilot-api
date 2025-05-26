package com.both.testing_pilot_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GithubUserResponse {

    private String provider = "github";
    private  Boolean isVerified = true;
    private  String email;
    private  String name;

    @JsonProperty("id")
    private String providerId;

    @JsonProperty("avatar_url")
    private String profileImage;
}
