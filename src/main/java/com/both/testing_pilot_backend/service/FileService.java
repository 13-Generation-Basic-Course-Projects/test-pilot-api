package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.model.FileMetaData;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
  FileMetaData uploadFile(MultipartFile file) throws IOException;
}
