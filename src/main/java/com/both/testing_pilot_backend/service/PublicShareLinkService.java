package com.both.testing_pilot_backend.service;


import com.both.testing_pilot_backend.model.PublicShareLink;

import java.util.UUID;

public interface PublicShareLinkService{
    PublicShareLink getPublicShareLinkById(UUID id);
}
