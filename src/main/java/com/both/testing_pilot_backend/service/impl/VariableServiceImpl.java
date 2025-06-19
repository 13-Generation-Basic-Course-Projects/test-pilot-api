package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.dto.request.VariableRequest;
import com.both.testing_pilot_backend.exceptions.BadRequestException;
import com.both.testing_pilot_backend.exceptions.NotFoundException;
import com.both.testing_pilot_backend.model.Variable;
import com.both.testing_pilot_backend.repository.VariableRepository;
import com.both.testing_pilot_backend.service.VariableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VariableServiceImpl implements VariableService {
    private final VariableRepository repository;

    @Override
    public Variable getVariablesByVariableId(UUID variableId) {
        if (variableId == null) {
            throw new BadRequestException("Variable ID must not be null.");
        }
        Variable variable = repository.getVariablesByVariableId(variableId);
        if (variable == null) {
            throw new NotFoundException("Variable not found with ID: " + variableId);
        }
        return variable;
    }

    @Override
    public List<Variable> getVariablesByProjectId(UUID projectId) {
        if (projectId == null) {
            throw new BadRequestException("Project ID must not be null.");
        }
        List<Variable> variables = repository.getVariablesByProjectId(projectId);
        if (variables == null || variables.isEmpty()) {
            throw new NotFoundException("No variables found for project ID: " + projectId);
        }
        return variables;
    }

    @Override
    public Variable saveVariable(VariableRequest request) {
        if (request == null) {
            throw new BadRequestException("VariableRequest must not be null.");
        }
        // Optional: Add validation on request fields here
        return repository.saveVariable(request);
    }

    @Override
    public Variable updateVariable(UUID variableId, VariableRequest request) {
        if (variableId == null) {
            throw new BadRequestException("Variable ID must not be null.");
        }
        if (request == null) {
            throw new BadRequestException("VariableRequest must not be null.");
        }
        Variable updated = repository.updateVariable(variableId, request);
        if (updated == null) {
            throw new NotFoundException("Variable not found with ID: " + variableId);
        }
        return updated;
    }

    @Override
    public void changeEnabled(UUID projectId, boolean isEnabled) {
        if (projectId == null) {
            throw new BadRequestException("Project ID must not be null.");
        }
        repository.changeEnabled(projectId, isEnabled);
    }

    @Override
    public Variable deleteVariable(UUID variableId) {
        if (variableId == null) {
            throw new BadRequestException("Variable ID must not be null.");
        }
        Variable deleted = repository.deleteVariable(variableId);
        if (deleted == null) {
            throw new NotFoundException("Variable not found with ID: " + variableId);
        }
        return deleted;
    }
}

