package com.xsslab.scenario05;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class FileService {

    public UploadResult storeFile(MultipartFile file, String category) {
        UploadResult result = new UploadResult();
        result.setFileId(UUID.randomUUID().toString().substring(0, 8));
        result.setStoredFilename(result.getFileId() + "_" + file.getOriginalFilename());
        result.setUploadTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return result;
    }

    public static class UploadResult {
        private String fileId;
        private String storedFilename;
        private String uploadTime;

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }

        public String getStoredFilename() {
            return storedFilename;
        }

        public void setStoredFilename(String storedFilename) {
            this.storedFilename = storedFilename;
        }

        public String getUploadTime() {
            return uploadTime;
        }

        public void setUploadTime(String uploadTime) {
            this.uploadTime = uploadTime;
        }
    }
}
