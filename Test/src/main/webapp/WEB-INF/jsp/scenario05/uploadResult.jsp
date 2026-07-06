<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>上传结果 - Scenario 05</title>
    <link rel="stylesheet" href="/css/common.css"/>
</head>
<body>
<div class="container narrow">
    <h1>上传结果</h1>
    <c:if test="${not empty error}">
        <div class="error-message">${error}</div>
    </c:if>
    <c:if test="${empty error}">
        <p>文件名: ${filename}</p>
        <p>存储名: ${storedFilename}</p>
        <p>大小: ${fileSize}</p>
        <p>类型: ${contentType}</p>
        <p>描述: ${description}</p>
        <p>上传时间: ${uploadTime}</p>
        <p>下载: <a href="${downloadUrl}">${downloadUrl}</a></p>
    </c:if>
    <a href="/scenario05/file/upload">继续上传</a>
</div>
</body>
</html>
