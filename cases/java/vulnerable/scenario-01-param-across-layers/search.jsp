<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>搜索结果 - 电商平台</title>
    <link rel="stylesheet" href="/css/search.css"/>
</head>
<body>
<div class="container">
    <h1>商品搜索</h1>

    <!-- 搜索表单 -->
    <form action="/search" method="get" class="search-form">
        <input type="text" name="q" value="${query}" placeholder="请输入搜索关键词"/>
        <button type="submit">搜索</button>
    </form>

    <!-- 搜索摘要 — 直接使用 EL 输出 displayContent -->
    <!-- 漏洞点: displayContent 包含用户原始输入，未经过 fn:escapeXml 转义 -->
    <div class="search-summary">
        ${result.displayContent}
    </div>

    <!-- 搜索建议 — 同样未转义 -->
    <c:if test="${not empty result.suggestion}">
        <div class="search-suggestion">
            ${result.suggestion}
        </div>
    </c:if>

    <!-- 商品列表 -->
    <div class="product-results">
        <c:forEach items="${result.products}" var="product">
            <div class="product-item">
                <span>${product}</span>
            </div>
        </c:forEach>
    </div>

    <!-- 额外: 搜索关键词回显 — 也未转义 -->
    <div class="search-meta">
        您当前搜索: ${result.searchQuery}
    </div>

    <!-- 分页 -->
    <div class="pagination">
        第 ${page} 页
    </div>
</div>
</body>
</html>
