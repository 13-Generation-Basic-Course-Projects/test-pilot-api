package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.dto.request.VariableRequest;
import com.both.testing_pilot_backend.model.Variable;

import java.util.List;
import java.util.UUID;

public interface VariableService {

//    List<Variable> getAllVariables();

    Variable getVariablesByVariableId(UUID variableId);

    List<Variable> getVariablesByProjectId(UUID projectId);

    Variable saveVariable(VariableRequest request);

    Variable updateVariable(UUID variableId, VariableRequest request);

    void changeEnabled(UUID projectId, boolean isEnabled);

    Variable deleteVariable(UUID variableId);

}
