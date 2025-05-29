package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.response.CustomApiResponse;
import com.both.testing_pilot_backend.model.FileMetaData;
import com.both.testing_pilot_backend.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/files")
@RequiredArgsConstructor
public class FileController {
  private final FileService fileService;


  @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<CustomApiResponse<FileMetaData>> uploadFile(@RequestParam MultipartFile file) throws IOException {
    FileMetaData fileMetaData = fileService.uploadFile(file);
    CustomApiResponse<FileMetaData> apiResponse = CustomApiResponse.<FileMetaData>builder()
            .success(true)
            .message("Upload file successfully!")
            .status(HttpStatus.CREATED)
            .data(fileMetaData)
            .build();
    return ResponseEntity.ok(apiResponse);
  }
}
