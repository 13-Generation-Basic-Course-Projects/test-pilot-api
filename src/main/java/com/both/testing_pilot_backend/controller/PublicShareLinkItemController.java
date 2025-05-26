package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.service.PublicShareLinkItemService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Public share link item", description = "Operations related to public share link item creation")
@RequiredArgsConstructor
@RequestMapping("/api/v1/public-share-link-item")
@SecurityRequirement(name = "bearerAuth")
public class PublicShareLinkItemController {
    private final PublicShareLinkItemService publicShareLinkItemService;
}
