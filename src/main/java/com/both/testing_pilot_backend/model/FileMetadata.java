package com.both.testing_pilot_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileMetadata {
  private String fileName;
  private String fileType;
  private String fileUrl;
  private Long fileSize;
}
