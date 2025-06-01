package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.model.FileMetadata;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

public interface FileService {

  FileMetadata uploadFile(MultipartFile file);

  InputStream getFileByFileName(String fileName);
}