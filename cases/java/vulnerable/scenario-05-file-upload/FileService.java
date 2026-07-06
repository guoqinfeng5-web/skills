package com.example.file.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 文件存储服务
 */
@Service
public class FileService {

    /**
     * 文件存储根目录
     */
    private static final String UPLOAD_DIR = "/data/uploads";

    /**
     * 上传结果封装
     */
    public static class UploadResult {
        private String fileId;
        private String storedFilename;
        private String originalFilename;
        private String uploadTime;
        private long size;

        public String getFileId() { return fileId; }
        public void setFileId(String fileId) { this.fileId = fileId; }

        public String getStoredFilename() { return storedFilename; }
        public void setStoredFilename(String storedFilename) { this.storedFilename = storedFilename; }

        public String getOriginalFilename() { return originalFilename; }
        public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

        public String getUploadTime() { return uploadTime; }
        public void setUploadTime(String uploadTime) { this.uploadTime = uploadTime; }

        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
    }

    /**
     * 存储上传文件
     * 
     * @param file     上传的 MultipartFile
     * @param category 文件分类
     * @return 上传结果
     */
    public UploadResult storeFile(MultipartFile file, String category) {
        try {
            // 生成唯一的存储文件名（实际存储时重命名了，但原始文件名仍可能被回显）
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String storedFilename = UUID.randomUUID().toString() + extension;

            // 创建分类目录
            String dateDir = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
            Path targetDir = Paths.get(UPLOAD_DIR, category, dateDir);
            Files.createDirectories(targetDir);

            // 写入文件
            Path targetPath = targetDir.resolve(storedFilename);
            file.transferTo(targetPath.toFile());

            // 返回上传结果
            UploadResult result = new UploadResult();
            result.setFileId(UUID.randomUUID().toString().substring(0, 8));
            result.setStoredFilename(storedFilename);
            result.setOriginalFilename(originalFilename);
            result.setUploadTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            result.setSize(file.getSize());

            // 记录上传日志（未转义的文件名写入日志）
            System.out.println("[UPLOAD] File uploaded: " + originalFilename 
                             + " -> " + storedFilename);

            return result;

        } catch (IOException e) {
            throw new RuntimeException("文件存储失败", e);
        }
    }
}
