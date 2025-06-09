package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.dto.request.VariableRequest;
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

//    @Override
//    public List<Variable> getAllVariables() {
//        return repository.getAllVariables();
//    }

    @Override
    public Variable getVariablesByVariableId(UUID variableId) {
        return repository.getVariablesByVariableId(variableId);
    }

    @Override
    public List<Variable> getVariablesByProjectId(UUID projectId) {
        return repository.getVariablesByProjectId(projectId);
    }

    @Override
    public Variable saveVariable(VariableRequest request) {
        return repository.saveVariable(request);
    }

    @Override
    public Variable updateVariable(UUID variableId, VariableRequest request) {
        return repository.updateVariable(variableId, request);
    }

    @Override
    public void changeEnabled(UUID projectId, boolean isEnabled) {
        repository.changeEnabled(projectId, isEnabled);
    }

    @Override
    public Variable deleteVariable(UUID variableId) {
        return repository.deleteVariable(variableId);
    }
}
