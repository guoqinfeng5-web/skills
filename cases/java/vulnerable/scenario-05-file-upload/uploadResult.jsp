<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>上传结果 - 文件管理系统</title>
    <link rel="stylesheet" href="/css/upload.css"/>
</head>
<body>
<div class="container">
    <h1>文件上传结果</h1>

    <!-- 错误提示 -->
    <c:if test="${not empty error}">
        <div class="error-message">
            <p><strong>上传失败:</strong> ${error}</p>
        </div>
    </c:if>

    <!-- 上传成功信息 -->
    <c:if test="${empty error}">
        <div class="success-message">
            <div class="success-icon">&#10004;</div>
            <h2>上传成功!</h2>

            <!-- 
                漏洞点: 文件名直接使用 EL 表达式输出
                如果文件名是 <script>alert(1)</script>.txt，将触发 XSS
            -->
            <div class="file-info">
                <p><strong>文件名称:</strong> ${filename}</p>         <!-- 未转义 -->
                <p><strong>文件大小:</strong> ${fileSize}</p>
                <p><strong>文件类型:</strong> ${contentType}</p>
                <p><strong>存储名称:</strong> ${storedFilename}</p>
                <p><strong>上传分类:</strong> ${category}</p>
                <p><strong>文件描述:</strong> ${description}</p>       <!-- 未转义 -->
                <p><strong>上传时间:</strong> ${uploadTime}</p>

                <!-- 下载链接 -->
                <p><a href="${downloadUrl}" class="download-link">下载文件</a></p>
            </div>
        </div>
    </c:if>

    <!-- 表单回显 — 文件名在 value 中回显也是风险点 -->
    <div class="file-info-display">
        <h3>上传信息确认</h3>
        <c:if test="${batchUpload}">
            <p>批量上传的文件名: ${filename}</p>  <!-- 批量上传拼接的文件名也要转义 -->
        </c:if>
    </div>

    <!-- 操作链接 -->
    <div class="actions">
        <a href="/file/upload">继续上传</a>
        <c:if test="${not empty downloadUrl}">
            | <a href="${downloadUrl}">下载文件</a>
        </c:if>
    </div>
</div>
</body>
</html>
