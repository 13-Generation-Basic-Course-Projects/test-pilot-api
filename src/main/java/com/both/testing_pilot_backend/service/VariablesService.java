package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.dto.request.VariablesRequest;
import com.both.testing_pilot_backend.model.Variables;

import java.util.List;
import java.util.UUID;

public interface VariablesService {

    List<Variables> getAllVariables();

    Variables getVariablesByVariableId(UUID variableId);

    List<Variables> getVariablesByProjectId(UUID projectId);

    Variables saveVariable(VariablesRequest request);

    Variables updateVariable(UUID variableId, VariablesRequest request);

    void changeEnabled(UUID projectId, boolean isEnabled);

    Variables deleteVariable(UUID variableId);

}
