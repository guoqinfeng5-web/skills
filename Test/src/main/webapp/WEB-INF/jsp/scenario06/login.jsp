<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>用户登录 - Scenario 06</title>
    <link rel="stylesheet" href="/css/common.css"/>
</head>
<body>
<div class="container narrow">
    <h1>企业门户登录 <span class="tag">Scenario 06</span></h1>

    <c:if test="${param.error == '1'}">
        <div class="error-message">用户名或密码错误，请重试</div>
    </c:if>

    <form action="/scenario06/auth/login" method="post">
        <div class="form-group">
            <label>用户名</label>
            <input type="text" name="username" required/>
        </div>
        <div class="form-group">
            <label>密码</label>
            <input type="password" name="password" required/>
        </div>
        <input type="hidden" name="redirect" value="${redirect}"/>
        <button type="submit">登录</button>
    </form>

    <c:if test="${not empty redirect}">
        <div class="redirect-info">
            <p>登录后将跳转到: ${redirect}</p>
        </div>
    </c:if>
</div>
</body>
</html>
