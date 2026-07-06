package com.xsslab.scenario05;

import com.xsslab.common.AuditTrailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/scenario05/file")
public class FileUploadController {

    private final FileService fileService;
    private final AuditTrailService auditTrail;

    public FileUploadController(FileService fileService, AuditTrailService auditTrail) {
        this.fileService = fileService;
        this.auditTrail = auditTrail;
    }

    @GetMapping("/upload")
    public String uploadPage() {
        return "scenario05/fileUpload";
    }

    @PostMapping("/upload")
    public String upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", defaultValue = "general") String category,
            @RequestParam(value = "description", required = false) String description,
            Model model,
            javax.servlet.http.HttpSession session) {

        String originalFilename = file.getOriginalFilename();
        String fileSize = formatFileSize(file.getSize());

        if (file.isEmpty()) {
            model.addAttribute("error", "上传文件不能为空");
            return "scenario05/uploadResult";
        }

        String contentType = file.getContentType();
        if (!isAllowedFileType(contentType)) {
            model.addAttribute("error", "不支持的文件类型: " + contentType);
            return "scenario05/uploadResult";
        }

        FileService.UploadResult result = fileService.storeFile(file, category);

        model.addAttribute("filename", originalFilename);
        model.addAttribute("storedFilename", result.getStoredFilename());
        model.addAttribute("fileSize", fileSize);
        model.addAttribute("contentType", contentType);
        model.addAttribute("category", category);
        model.addAttribute("description", description);
        model.addAttribute("downloadUrl", "/scenario05/file/download/" + result.getFileId());
        model.addAttribute("uploadTime", result.getUploadTime());

        auditTrail.trace("FILE_UPLOAD", session,
                "上传文件: " + originalFilename,
                "desc=" + description + ", stored=" + result.getStoredFilename());

        return "scenario05/uploadResult";
    }

    @PostMapping("/upload/batch")
    public String uploadBatch(
            @RequestParam("files") List<MultipartFile> files,
            Model model) {

        StringBuilder uploadedNames = new StringBuilder("成功上传: ");
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                uploadedNames.append(file.getOriginalFilename()).append(", ");
                fileService.storeFile(file, "batch");
            }
        }
        model.addAttribute("filename", uploadedNames.toString());
        model.addAttribute("batchUpload", true);
        return "scenario05/uploadResult";
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }

    private boolean isAllowedFileType(String contentType) {
        if (contentType == null) return false;
        return contentType.startsWith("image/")
            || "application/pdf".equals(contentType)
            || "text/plain".equals(contentType);
    }
}
