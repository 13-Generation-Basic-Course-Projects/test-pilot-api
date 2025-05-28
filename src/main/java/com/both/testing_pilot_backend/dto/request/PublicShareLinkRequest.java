package com.both.testing_pilot_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicShareLinkRequest {
    private String token;
    private String sharedItemType;
    private UUID sharedItemId;
    private LocalDateTime expireAt;
}
