package com.example.feedback.controller;

import com.example.feedback.model.FeedbackRequest;
import com.example.feedback.model.FeedbackResponse;
import com.example.feedback.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 用户反馈/评论 REST 控制器
 * 
 * 注意: 此类直接返回 HTML 片段而非 JSON，存在 XSS 风险
 */
@Controller
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    /**
     * 提交反馈 — 存在 XSS 漏洞
     * 
     * @param request 包含用户输入的评论内容
     * @return HTML 片段（包含未转义的用户内容）
     */
    @PostMapping(value = "/submit", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String submitFeedback(@RequestBody FeedbackRequest request) {

        // 1. 获取用户提交的内容
        String content = request.getContent();
        String username = request.getUsername();

        // 2. 调用服务层处理（服务层做了业务处理但未转义）
        FeedbackResponse processed = feedbackService.processFeedback(
                content, username);

        // 3. 直接拼接 HTML 返回 — 漏洞点！
        StringBuilder html = new StringBuilder();
        html.append("<div class=\"feedback-item\">");
        html.append("  <div class=\"feedback-header\">");
        html.append("    <span class=\"username\">").append(processed.getDisplayUser()).append("</span>");
        html.append("    <span class=\"time\">").append(processed.getFormattedTime()).append("</span>");
        html.append("  </div>");
        html.append("  <div class=\"feedback-content\">");
        html.append("    ").append(processed.getDisplayContent());  // 未转义!
        html.append("  </div>");
        html.append("  <div class=\"feedback-actions\">");
        html.append("    <a href=\"#\" class=\"reply-btn\" data-id=\"")
            .append(processed.getFeedbackId()).append("\">回复</a>");
        html.append("  </div>");
        html.append("</div>");

        return html.toString();
    }

    /**
     * 获取反馈列表 — 也存在同样的问题
     */
    @GetMapping(value = "/list", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String listFeedback(@RequestParam(value = "page", defaultValue = "1") int page) {
        // 实际项目会从 DB 查询，但数据源头依然是用户提交的未转义内容
        StringBuilder html = new StringBuilder();
        html.append("<div class=\"feedback-list\">");
        // 假设从数据库获取了 feedbackService.getLatestFeedbacks(page)
        for (FeedbackResponse fb : feedbackService.getLatestFeedbacks(page)) {
            // 同样未转义 — 漏洞点
            html.append("<div class=\"feedback-item\">");
            html.append("  <div class=\"feedback-content\">")
                .append(fb.getDisplayContent())  // 未转义！
                .append("  </div>");
            html.append("</div>");
        }
        html.append("</div>");
        return html.toString();
    }

    /**
     * 提交简单回复 — 直接返回成功消息（含用户输入）
     */
    @PostMapping(value = "/reply", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String submitReply(
            @RequestParam("feedbackId") String feedbackId,
            @RequestParam("replyContent") String replyContent) {

        // 将回复内容也拼接成 HTML 返回 — 又一个漏洞点
        return "<div class=\"reply-item\" data-parent=\"" + feedbackId + "\">"
             + "  <span class=\"reply-text\">" + replyContent + "</span>"  // 未转义！
             + "</div>";
    }
}
