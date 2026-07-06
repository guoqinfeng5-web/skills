<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>登录失败 - Scenario 02</title>
    <link rel="stylesheet" href="/css/common.css"/>
</head>
<body>
<div class="container narrow">
    <h1>登录失败</h1>
    <div class="error-message">
        <p><strong>错误信息:</strong></p>
        <p class="msg-content"><%= request.getAttribute("errorMsg") %></p>
    </div>
    <div class="user-info">
        <p>输入的用户名: <%= request.getAttribute("loginName") %></p>
        <p>剩余尝试次数: <%= request.getAttribute("remainingAttempts") %></p>
    </div>
    <a href="/scenario02/auth/login">返回登录</a>
</div>
</body>
</html>
