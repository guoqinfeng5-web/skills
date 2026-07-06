<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>日志管理 - Scenario 07</title>
    <link rel="stylesheet" href="/css/common.css"/>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
</head>
<body>
<div class="container wide">
    <h1>日志管理 <span class="tag">Scenario 07</span></h1>

    <div class="stats-bar">
        <span>总日志: ${stats.totalCount}</span>
        <span>INFO: ${stats.infoCount}</span>
        <span>WARN: ${stats.warnCount}</span>
        <span>ERROR: ${stats.errorCount}</span>
    </div>

    <form action="/scenario07/admin/logs" method="get" class="filter-form">
        <select name="level">
            <option value="ALL" ${currentLevel == 'ALL' ? 'selected' : ''}>全部</option>
            <option value="INFO" ${currentLevel == 'INFO' ? 'selected' : ''}>INFO</option>
            <option value="WARN" ${currentLevel == 'WARN' ? 'selected' : ''}>WARN</option>
            <option value="ERROR" ${currentLevel == 'ERROR' ? 'selected' : ''}>ERROR</option>
        </select>
        <input type="text" name="keyword" placeholder="搜索日志" value="${keyword}"/>
        <button type="submit">过滤</button>
    </form>

    <div class="record-form">
        <h3>写入测试日志（模拟用户输入被记录）</h3>
        <input type="text" id="logMessage" placeholder="日志消息"/>
        <input type="text" id="logDetail" placeholder="日志详情"/>
        <button id="recordBtn">记录</button>
    </div>

    <input type="text" id="searchKeyword" placeholder="AJAX 搜索"/>
    <button id="searchBtn">搜索</button>

    <table class="log-table">
        <thead>
            <tr>
                <th>ID</th><th>级别</th><th>操作</th><th>用户</th>
                <th>消息</th><th>详情</th><th>时间</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${logs}" var="log">
                <tr>
                    <td>${log.id}</td>
                    <td>${log.level}</td>
                    <td>${log.action}</td>
                    <td>${log.user}</td>
                    <td class="log-message">${log.message}</td>
                    <td class="log-detail">${log.detail}</td>
                    <td>${log.formattedTime}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <div id="logDetailPanel"></div>

    <c:if test="${not empty logDetail}">
        <div class="log-detail-panel detail-content">
            <h3>日志详情</h3>
            <p><strong>消息:</strong> ${logDetail.message}</p>
            <p><strong>详情:</strong> ${logDetail.detail}</p>
        </div>
    </c:if>
</div>
<script src="/js/scenario07-log-viewer.js"></script>
</body>
</html>
