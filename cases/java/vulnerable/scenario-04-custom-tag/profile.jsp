<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/user" prefix="u" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>用户个人资料 - 社区平台</title>
    <link rel="stylesheet" href="/css/profile.css"/>
</head>
<body>
<div class="container">
    <h1>用户个人资料</h1>

    <!-- 使用自定义标签渲染用户信息 -->
    <!-- 
        漏洞点: profileUser 是用户输入（URL 参数），直接传入自定义标签
        而自定义标签内部使用 out.print() 直接输出了属性值，未做转义
    -->
    <div class="profile-section">
        <h2>基本信息</h2>
        <u:userProfile nickName="${profileUser}" avatar="/avatars/default.png"/>
    </div>

    <!-- 另一个使用相同标签的地方，传入用户简介 -->
    <div class="profile-section">
        <h2>用户简介</h2>
        <u:userProfile nickName="${userBio}" avatar="${userAvatar}"/>
    </div>

    <!-- 无法使用自定义标签的情况 —— 回退到 EL 直接输出 -->
    <div class="profile-section">
        <h2>其他信息</h2>
        <p>用户名: ${profileUser}</p>  <!-- 也未经转义 -->
        <p>显示名: ${displayName}</p>  <!-- 也未经转义 -->
    </div>

    <!-- 导航标签 -->
    <div class="profile-tabs">
        <a href="/profile?username=${profileUser}&tab=info" 
           class="tab ${currentTab == 'info' ? 'active' : ''}">基本信息</a>
        <a href="/profile?username=${profileUser}&tab=photos" 
           class="tab ${currentTab == 'photos' ? 'active' : ''}">相册</a>
        <a href="/profile?username=${profileUser}&tab=settings" 
           class="tab ${currentTab == 'settings' ? 'active' : ''}">设置</a>
    </div>
</div>
</body>
</html>
