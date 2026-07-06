package com.example.log.model;

import java.util.Date;

/**
 * 日志条目模型
 */
public class LogEntry {

    private Long id;
    private String action;
    private String user;
    private String message;       // 日志消息（可能包含用户输入）
    private String detail;        // 日志详情（可能包含用户输入）
    private String level;
    private Date timestamp;
    private String ipAddress;
    private String userAgent;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    /**
     * 获取格式化的时间字符串
     */
    public String getFormattedTime() {
        if (timestamp == null) return "";
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
    }
}
