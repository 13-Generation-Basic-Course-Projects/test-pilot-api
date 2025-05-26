package com.both.testing_pilot_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicShareLinkItemRequest {
    private String itemType;
    private UUID itemId;
    private UUID shareLinkId;
}
