<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/tags/userProfile.tld" prefix="u" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>用户资料 - Scenario 04</title>
    <link rel="stylesheet" href="/css/common.css"/>
</head>
<body>
<div class="container">
    <h1>用户资料 <span class="tag">Scenario 04</span></h1>

    <u:userProfile nickName="${profileUser}" level="${userLevel}" avatar="${userAvatar}"/>

    <div class="profile-extra">
        <p>显示名称: ${displayName}</p>
        <p>个人简介: ${userBio}</p>
    </div>
</div>
</body>
</html>
