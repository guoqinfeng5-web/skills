package com.example.file.controller;

import com.example.file.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传控制器
 * 
 * 提供文件上传功能，上传后显示文件信息和上传结果
 */
@Controller
@RequestMapping("/file")
public class FileUploadController {

    @Autowired
    private FileService fileService;

    /**
     * 显示文件上传页面
     */
    @GetMapping("/upload")
    public String uploadPage() {
        return "fileUpload";
    }

    /**
     * 处理单个文件上传
     * 
     * @param file     上传的文件（包含用户可控的文件名）
     * @param category 文件分类
     * @param model    视图模型
     * @return 上传结果页面
     */
    @PostMapping("/upload")
    public String upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", defaultValue = "general") String category,
            @RequestParam(value = "description", required = false) String description,
            Model model) {

        // ---- 获取用户输入（漏洞数据源） ----
        // 文件名是用户可控的 —— 通过 getOriginalFilename() 获取
        // 用户可以构造一个包含恶意脚本的文件名
        String originalFilename = file.getOriginalFilename();
        String fileSize = formatFileSize(file.getSize());

        // ---- 文件内容安全检查（但不检查文件名！） ----
        if (file.isEmpty()) {
            model.addAttribute("error", "上传文件不能为空");
            return "uploadResult";
        }

        // 检查文件类型（只检查了文件内容类型，未处理文件名）
        String contentType = file.getContentType();
        if (!isAllowedFileType(contentType)) {
            model.addAttribute("error", "不支持的文件类型: " + contentType);
            return "uploadResult";
        }

        // ---- 存储文件（服务层处理） ----
        FileService.UploadResult result = fileService.storeFile(file, category);

        // ---- 将信息传入视图（漏洞点：未转义） ----
        model.addAttribute("filename", originalFilename);       // 未转义！
        model.addAttribute("storedFilename", result.getStoredFilename());
        model.addAttribute("fileSize", fileSize);
        model.addAttribute("contentType", contentType);
        model.addAttribute("category", category);
        model.addAttribute("description", description);          // 未转义！
        model.addAttribute("downloadUrl", "/file/download/" + result.getFileId());
        model.addAttribute("uploadTime", result.getUploadTime());

        return "uploadResult";
    }

    /**
     * 批量上传文件
     */
    @PostMapping("/upload/batch")
    public String uploadBatch(
            @RequestParam("files") List<MultipartFile> files,
            Model model) {

        StringBuilder uploadedNames = new StringBuilder("成功上传: ");

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                // 直接拼接文件名 — 漏洞点
                uploadedNames.append(file.getOriginalFilename()).append(", ");  // 未转义！
                fileService.storeFile(file, "batch");
            }
        }

        model.addAttribute("filename", uploadedNames.toString());  // 未转义！
        model.addAttribute("batchUpload", true);

        return "uploadResult";
    }

    /**
     * 文件下载
     */
    @GetMapping("/download/{fileId}")
    public String download(@PathVariable("fileId") String fileId, Model model) {
        // 实际项目会从数据库查询并返回文件流
        model.addAttribute("filename", "file_" + fileId + ".bin");
        return "fileDownload";
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }

    /**
     * 检查文件类型是否允许
     */
    private boolean isAllowedFileType(String contentType) {
        if (contentType == null) return false;
        return contentType.startsWith("image/") 
            || contentType.equals("application/pdf")
            || contentType.equals("text/plain");
    }
}
