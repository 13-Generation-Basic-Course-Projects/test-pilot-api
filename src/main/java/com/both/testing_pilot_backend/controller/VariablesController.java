package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.request.VariablesRequest;
import com.both.testing_pilot_backend.model.Variables;
import com.both.testing_pilot_backend.service.VariablesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/variables")
@Tag(name = "Variables", description = "Project variables")
public class VariablesController {

    private final VariablesService variablesService;

    @Autowired
    public VariablesController(VariablesService variablesService) {
        this.variablesService = variablesService;
    }

    @Operation(summary = "Get all variables")
    @GetMapping
    public ResponseEntity<List<Variables>> getAllVariables() {
        return ResponseEntity.ok(variablesService.getAllVariables());
    }

    @Operation(summary = "Get variable by variable ID")
    @GetMapping("/{id}")
    public ResponseEntity<Variables> getVariableById(@PathVariable UUID id) {
        Variables variable = variablesService.getVariablesByVariableId(id);
        return variable != null ? ResponseEntity.ok(variable) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get variables by project ID")
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Variables>> getVariablesByProjectId(@PathVariable UUID projectId) {
        return ResponseEntity.ok(variablesService.getVariablesByProjectId(projectId));
    }

    @Operation(summary = "Create a new variable")
    @PostMapping
    public ResponseEntity<Variables> createVariable(@Valid @RequestBody VariablesRequest request) {
        return ResponseEntity.ok(variablesService.saveVariable(request));
    }

    @Operation(summary = "Update a variable")
    @PutMapping("/{id}")
    public ResponseEntity<Variables> updateVariable(@PathVariable UUID id, @Valid @RequestBody VariablesRequest request) {
        return ResponseEntity.ok(variablesService.updateVariable(id, request));
    }

    @Operation(summary = "Enable or disable variable")
    @PatchMapping("/enable")
    public ResponseEntity<Void> updateEnabledStatus(@RequestParam UUID projectId, @RequestParam boolean isEnabled) {
        variablesService.changeEnabled(projectId, isEnabled);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a variable by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Variables> deleteVariable(@PathVariable UUID id) {
        return ResponseEntity.ok(variablesService.deleteVariable(id));
    }
}