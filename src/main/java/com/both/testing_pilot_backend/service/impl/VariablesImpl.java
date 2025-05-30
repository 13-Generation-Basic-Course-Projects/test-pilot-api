package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.dto.request.VariablesRequest;
import com.both.testing_pilot_backend.model.Variables;
import com.both.testing_pilot_backend.repository.VariablesRepository;
import com.both.testing_pilot_backend.service.VariablesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VariablesImpl implements VariablesService {

    private final VariablesRepository repository;

    @Autowired
    public VariablesImpl(VariablesRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Variables> getAllVariables() {
        return repository.getAllVariables();
    }

    @Override
    public Variables getVariablesByVariableId(UUID variableId) {
        return repository.getVariablesByVariableId(variableId);
    }

    @Override
    public List<Variables> getVariablesByProjectId(UUID projectId) {
        return repository.getVariablesByProjectId(projectId);
    }

    @Override
    public Variables saveVariable(VariablesRequest request) {
        Variables variable = new Variables();
        variable.setKeyName(request.getKeyName());
        variable.setKeyValue(request.getKeyValue());
        variable.setEnabled(request.isEnabled());
        variable.setProjectId(request.getProjectId());
        return repository.saveVariable(variable);
    }

    @Override
    public Variables updateVariable(UUID variableId, VariablesRequest request) {
        Variables variable = new Variables();
        variable.setVariableId(variableId);
        variable.setKeyName(request.getKeyName());
        variable.setKeyValue(request.getKeyValue());
        variable.setEnabled(request.isEnabled());
        variable.setProjectId(request.getProjectId());
        return repository.updateVariable(variable);
    }

    @Override
    public void changeEnabled(UUID projectId, boolean isEnabled) {
        repository.changeEnabled(projectId, isEnabled);
    }

    @Override
    public Variables deleteVariable(UUID variableId) {
        return repository.deleteVariable(variableId);
    }
}
