<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>用户登录 - 企业门户</title>
    <link rel="stylesheet" href="/css/login.css"/>
</head>
<body>
<div class="login-container">
    <div class="login-box">
        <h1>企业门户登录</h1>

        <!-- 显示错误信息 -->
        <c:if test="${param.error == '1'}">
            <div class="alert alert-error">
                用户名或密码错误，请重试
            </div>
        </c:if>

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

            <!-- 
                漏洞点: 在隐藏表单中回显 redirect 参数
                EL 表达式 ${redirect} 直接输出用户输入的 redirect 值
                如果 redirect 包含 "><script>alert(1)</script>，将闭合 input 标签
            -->
            <input type="hidden" name="redirect" value="${redirect}"/>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">登 录</button>
            </div>
        </form>

        <div class="login-footer">
            <a href="/auth/register">注册新账号</a>
            <span class="separator">|</span>
            <a href="/auth/forgot">忘记密码?</a>
        </div>
    </div>

    <!-- 又一处 redirect 输出 — 用于显示当前重定向目标 -->
    <c:if test="${not empty redirect}">
        <div class="redirect-info">
            <p>登录后将跳转到: ${redirect}</p>
        </div>
    </c:if>
</div>
</body>
</html>
