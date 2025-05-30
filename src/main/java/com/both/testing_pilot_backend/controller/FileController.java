package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.response.CustomApiResponse;
import com.both.testing_pilot_backend.model.FileMetadata;
import com.both.testing_pilot_backend.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

@RestController
@RequestMapping("api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse<FileMetadata>> uploadFile(@RequestParam MultipartFile file){
        FileMetadata fileMetadata = fileService.uploadFile(file);
        CustomApiResponse<FileMetadata> apiResponse = CustomApiResponse.<FileMetadata>builder()
                .success(true)
                .message("Upload file successfully!")
                .status(HttpStatus.CREATED)
                .data(fileMetadata)
                .timestamps(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/preview-file/{file-name}")
    public ResponseEntity<?> getFileByFileName(@PathVariable("file-name") String fileName) throws IOException {
        InputStream inputStream = fileService.getFileByFileName(fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.IMAGE_PNG)
                .body(inputStream.readAllBytes());
    }

}