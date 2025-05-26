package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.response.CustomApiResponse;
import com.both.testing_pilot_backend.model.TestCase;
import com.both.testing_pilot_backend.service.TestcaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/testcases")
@RequiredArgsConstructor
public class TestcaseController {

    private final TestcaseService testcaseService;

    @GetMapping("/{testcaseId}")
    public ResponseEntity<CustomApiResponse> getTestcaseById(
            @PathVariable("testcaseId") UUID testcaseId) {


        TestCase testCase = testcaseService.getTestCaseById(testcaseId);

        CustomApiResponse apiResponse = CustomApiResponse.builder()
                .message("Testcase has been fetched successfully")
                .status(HttpStatus.OK)
                .success(true)
                .data(testCase)
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
