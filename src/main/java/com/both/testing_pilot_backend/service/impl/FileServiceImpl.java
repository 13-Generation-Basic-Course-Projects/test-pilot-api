package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.model.FileMetaData;
import com.both.testing_pilot_backend.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    @Value("${spring.file-upload-path}")
    private String pathName;

    @Override
    public FileMetaData uploadFile(MultipartFile file) throws IOException {
        Path rootPath = Paths.get(pathName);

        if(!Files.exists(rootPath)){
          Files.createDirectories(rootPath);
        }

        return null;
    }
}
