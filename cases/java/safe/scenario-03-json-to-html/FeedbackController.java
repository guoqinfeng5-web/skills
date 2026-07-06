package com.xss.safe.scenario03;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

/**
 * Safe version: returns JSON data; no HTML string concatenation.
 */
@RestController
public class FeedbackController {

    private final FeedbackService feedbackService = new FeedbackService();

    @PostMapping("/api/feedback")
    public FeedbackResponse submitFeedback(@RequestParam String message) {
        String safe = HtmlUtils.htmlEscape(message);
        String processed = feedbackService.process(safe);
        FeedbackResponse resp = new FeedbackResponse();
        resp.setContent(processed);
        resp.setStatus("ok");
        return resp;
    }

    public static class FeedbackResponse {
        private String content;
        private String status;
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
