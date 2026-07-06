<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>欢迎登录 - 企业管理系统</title>
    <link rel="stylesheet" href="/css/login.css"/>
</head>
<body>
<div class="login-container">
    <div class="login-box">
        <h1>企业管理系统</h1>
        <p class="subtitle">请使用您的账号登录</p>

        <form action="/auth/login" method="post" class="login-form">
            <div class="form-group">
                <label for="username">用户名</label>
                <input type="text" id="username" name="username" 
                       placeholder="请输入用户名" required
                       class="form-control"/>
            </div>
            <div class="form-group">
                <label for="password">密码</label>
                <input type="password" id="password" name="password" 
                       placeholder="请输入密码" required
                       class="form-control"/>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">登 录</button>
            </div>
        </form>

        <div class="login-footer">
            <a href="/auth/forgot">忘记密码?</a>
        </div>
    </div>
</div>
</body>
</html>
