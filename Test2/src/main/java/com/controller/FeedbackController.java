// File: src/main/java/com/example/demo/controller/FeedbackController.java
package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FeedbackController {

    @Autowired
    private FeedbackRepository repository;

    // 业务 1：提交反馈
    @PostMapping("/feedback/submit")
    public ResponseEntity<?> submitFeedback(@RequestBody Feedback feedback) {
        // 假设 Feedback 实体包含 String title 和 String content
        repository.save(feedback);
        return ResponseEntity.ok("Feedback saved successfully.");
    }

    // 业务 2：获取反馈列表
    @GetMapping("/feedback/list")
    public List<Feedback> getFeedbacks() {
        return repository.findAll();
    }

    // 业务 3：兼容老系统的全局通知下发接口 (历史遗留)
    @GetMapping("/system/notice")
    public void showNotice(@RequestParam("msg") String msg, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        // 直接返回了带有基本样式的 HTML 提示
        out.print("<html><body style='padding:20px;'><div class='alert-box'>系统提示: " + msg + "</div></body></html>");
    }
}