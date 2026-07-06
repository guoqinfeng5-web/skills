<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>搜索结果 - Scenario 01</title>
    <link rel="stylesheet" href="/css/common.css"/>
</head>
<body>
<div class="container">
    <h1>商品搜索 <span class="tag">Scenario 01</span></h1>
    <form action="/scenario01/search" method="get" class="search-form">
        <input type="text" name="q" value="${query}" placeholder="请输入搜索关键词"/>
        <button type="submit">搜索</button>
    </form>

    <div class="search-summary">
        ${result.displayContent}
    </div>

    <c:if test="${not empty result.suggestion}">
        <div class="search-suggestion">
            ${result.suggestion}
        </div>
    </c:if>

    <div class="product-results">
        <c:forEach items="${result.products}" var="product">
            <div class="product-item"><span>${product}</span></div>
        </c:forEach>
    </div>

    <div class="search-meta">您当前搜索: ${result.searchQuery}</div>
    <div class="pagination">第 ${page} 页</div>
</div>
</body>
</html>
