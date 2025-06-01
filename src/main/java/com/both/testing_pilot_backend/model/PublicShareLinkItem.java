package com.both.testing_pilot_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicShareLinkItem {
    private UUID shareLinkItemId;
    private String itemType;
    private UUID itemId;
    private PublicShareLink shareLinkId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
