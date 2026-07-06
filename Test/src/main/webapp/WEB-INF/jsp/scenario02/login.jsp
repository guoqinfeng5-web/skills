<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>登录 - Scenario 02</title>
    <link rel="stylesheet" href="/css/common.css"/>
</head>
<body>
<div class="container narrow">
    <h1>用户登录 <span class="tag">Scenario 02</span></h1>
    <form action="/scenario02/auth/login" method="post">
        <div class="form-group">
            <label>用户名</label>
            <input type="text" name="username" required/>
        </div>
        <div class="form-group">
            <label>密码</label>
            <input type="password" name="password" required/>
        </div>
        <button type="submit">登录</button>
    </form>
    <p class="hint">正确凭据: admin / admin123</p>
</div>
</body>
</html>
