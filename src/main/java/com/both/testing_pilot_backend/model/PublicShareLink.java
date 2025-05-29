package com.both.testing_pilot_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicShareLink {
    private UUID shareLinkId;
    private String token;
    private String sharedItemType;
    private UUID sharedItemId;
    private LocalDateTime expireAt;
    private User createdByUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
