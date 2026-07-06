<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>登录失败 - 企业管理系统</title>
    <link rel="stylesheet" href="/css/error.css"/>
</head>
<body>
<div class="error-container">
    <div class="error-box">
        <h1>登录失败</h1>

        <!-- 错误图标 -->
        <div class="error-icon">&#10060;</div>

        <!-- 
            漏洞点: 使用 <%= %> 直接输出未转义的错误信息
            <%= request.getAttribute("errorMsg") %> 不会自动转义 HTML
            如果 username 包含 <script>alert(1)</script>，脚本将执行
        -->
        <div class="error-message">
            <p><strong>错误信息:</strong></p>
            <p class="msg-content">
                <%= request.getAttribute("errorMsg") %>
            </p>
        </div>

        <!-- 另一个漏洞点: 直接输出用户名 (同样未转义) -->
        <div class="user-info">
            <p>输入的用户名: <%= request.getAttribute("loginName") %></p>
            <p>剩余尝试次数: <%= request.getAttribute("remainingAttempts") %></p>
        </div>

        <!-- 安全提示 (非漏洞) -->
        <div class="error-actions">
            <a href="/auth/login" class="btn btn-primary">返回登录</a>
        </div>
    </div>
</div>
</body>
</html>
