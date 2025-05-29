package com.both.testing_pilot_backend.util;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class VerificationCodeStorage {

    private final Map<UUID, String> codeMap = new ConcurrentHashMap<>();

    public void storeCode(UUID projectCollaboratorId, String code) {
        codeMap.put(projectCollaboratorId, code);
    }

    public String getCode(UUID projectCollaboratorId) {
        return codeMap.get(projectCollaboratorId);
    }

    public void removeCode(UUID projectCollaboratorId) {
        codeMap.remove(projectCollaboratorId);
    }
}
