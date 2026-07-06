package com.example.feedback.model;

/**
 * 反馈响应体 —— 包含展示内容
 */
public class FeedbackResponse {

    private Long feedbackId;
    private String displayContent;  // 直接用于 HTML 展示的内容
    private String displayUser;     // 展示用的用户名
    private String formattedTime;   // 格式化后的时间
    private String rawContent;      // 原始内容

    public Long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getDisplayContent() {
        return displayContent;
    }

    public void setDisplayContent(String displayContent) {
        this.displayContent = displayContent;
    }

    public String getDisplayUser() {
        return displayUser;
    }

    public void setDisplayUser(String displayUser) {
        this.displayUser = displayUser;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }
}
