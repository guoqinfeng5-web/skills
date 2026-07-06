package com.xss.safe.scenario05;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Safe version: saves file and returns the path.
 */
public class FileService {

    private static final String UPLOAD_DIR = "/tmp/uploads/";

    public String saveFile(org.springframework.web.multipart.MultipartFile file, String safeName) {
        try {
            Path target = Path.of(UPLOAD_DIR, safeName);
            Files.createDirectories(target.getParent());
            file.transferTo(target.toFile());
            return target.toString();
        } catch (Exception e) {
            throw new RuntimeException("File save failed", e);
        }
    }
}
