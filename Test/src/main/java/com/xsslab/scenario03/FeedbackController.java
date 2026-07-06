package com.xsslab.scenario03;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/scenario03/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping(value = "/submit", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String submitFeedback(@RequestBody FeedbackRequest request) {
        FeedbackResponse processed = feedbackService.processFeedback(
                request.getContent(), request.getUsername());

        StringBuilder html = new StringBuilder();
        html.append("<div class=\"feedback-item\">");
        html.append("  <div class=\"feedback-header\">");
        html.append("    <span class=\"username\">").append(processed.getDisplayUser()).append("</span>");
        html.append("    <span class=\"time\">").append(processed.getFormattedTime()).append("</span>");
        html.append("  </div>");
        html.append("  <div class=\"feedback-content\">");
        html.append("    ").append(processed.getDisplayContent());
        html.append("  </div>");
        html.append("</div>");
        return html.toString();
    }

    @GetMapping(value = "/list", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String listFeedback(@RequestParam(value = "page", defaultValue = "1") int page) {
        StringBuilder html = new StringBuilder();
        html.append("<div class=\"feedback-list\">");
        for (FeedbackResponse fb : feedbackService.getLatestFeedbacks(page)) {
            html.append("<div class=\"feedback-item\">");
            html.append("  <div class=\"feedback-content\">")
                .append(fb.getDisplayContent())
                .append("  </div>");
            html.append("</div>");
        }
        html.append("</div>");
        return html.toString();
    }

    @PostMapping(value = "/reply", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String submitReply(
            @RequestParam("feedbackId") String feedbackId,
            @RequestParam("replyContent") String replyContent) {
        return "<div class=\"reply-item\" data-parent=\"" + feedbackId + "\">"
             + "  <span class=\"reply-text\">" + replyContent + "</span>"
             + "</div>";
    }
}
