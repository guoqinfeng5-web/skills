package com.xss.safe.scenario05;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

/**
 * Safe version: escapes filename before passing to view.
 */
@Controller
public class FileUploadController {

    private final FileService fileService = new FileService();

    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file, javax.servlet.http.HttpServletRequest request) {
        String originalName = file.getOriginalFilename();
        // Safe: escape the filename before putting it in the model
        String safeName = HtmlUtils.htmlEscape(originalName);
        String savedPath = fileService.saveFile(file, safeName);
        request.setAttribute("fileName", safeName);
        request.setAttribute("savedPath", savedPath);
        return "uploadResult";
    }
}
