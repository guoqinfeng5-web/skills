<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>日志管理 - 管理后台</title>
    <link rel="stylesheet" href="/css/admin.css"/>
</head>
<body>
<div class="admin-container">
    <!-- 管理后台顶部导航 -->
    <header class="admin-header">
        <h1>系统管理后台</h1>
        <nav>
            <a href="/admin/dashboard">控制台</a>
            <a href="/admin/users">用户管理</a>
            <a href="/admin/logs" class="active">日志管理</a>
            <a href="/admin/settings">系统设置</a>
        </nav>
        <div class="user-info">
            当前管理员: ${sessionScope.currentUser}
        </div>
    </header>

    <div class="admin-content">
        <h2>日志管理</h2>

        <!-- 统计信息 -->
        <div class="stats-bar">
            <span>总日志: ${stats.totalCount}</span>
            <span>INFO: ${stats.infoCount}</span>
            <span>WARN: ${stats.warnCount}</span>
            <span>ERROR: ${stats.errorCount}</span>
        </div>

        <!-- 日志过滤表单 -->
        <form action="/admin/logs" method="get" class="filter-form">
            <select name="level">
                <option value="ALL" ${currentLevel == 'ALL' ? 'selected' : ''}>全部级别</option>
                <option value="INFO" ${currentLevel == 'INFO' ? 'selected' : ''}>INFO</option>
                <option value="WARN" ${currentLevel == 'WARN' ? 'selected' : ''}>WARN</option>
                <option value="ERROR" ${currentLevel == 'ERROR' ? 'selected' : ''}>ERROR</option>
                <option value="DEBUG" ${currentLevel == 'DEBUG' ? 'selected' : ''}>DEBUG</option>
            </select>
            <input type="text" name="keyword" placeholder="搜索日志内容" value="${keyword}"/>
            <button type="submit">过滤</button>
        </form>

        <!-- 
            日志列表 — 使用 EL 直接展示日志内容
            漏洞点: 日志消息和详情可能包含用户输入
                   没有使用 <c:out> 或 fn:escapeXml() 进行转义
        -->
        <table class="log-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>级别</th>
                    <th>操作</th>
                    <th>用户</th>
                    <th>日志消息</th>
                    <th>详情</th>
                    <th>时间</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${logs}" var="log" varStatus="loop">
                    <tr class="log-row log-level-${log.level}">
                        <td>${log.id}</td>
                        <td><span class="level-badge">${log.level}</span></td>
                        <td>${log.action}</td>
                        <td>${log.user}</td>
                        <!-- 
                            漏洞点: 日志消息直接输出
                            ${log.message} 可能包含 <script> 等恶意内容
                        -->
                        <td class="log-message">${log.message}</td>
                        
                        <!-- 
                            漏洞点: 日志详情直接输出（用户输入内容）
                            使用 EL 表达式 ${log.detail} 输出
                        -->
                        <td class="log-detail">${log.detail}</td>
                        
                        <td>${log.formattedTime}</td>
                        <td>
                            <a href="/admin/logs/detail/${log.id}">查看</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <!-- 日志详情区域（AJAX 加载） -->
        <c:if test="${not empty logDetail}">
            <div class="log-detail-panel">
                <h3>日志详情</h3>
                <div class="detail-content">
                    <!-- 
                        漏洞点: 日志详情直接 EL 输出
                        包含用户未转义的内容
                    -->
                    <p><strong>消息:</strong> ${logDetail.message}</p>
                    <p><strong>详情:</strong> ${logDetail.detail}</p>
                    <p><strong>用户:</strong> ${logDetail.user}</p>
                    <p><strong>IP:</strong> ${logDetail.ipAddress}</p>
                    <p><strong>时间:</strong> ${logDetail.formattedTime}</p>
                    <p><strong>UA:</strong> ${logDetail.userAgent}</p>
                </div>
            </div>
        </c:if>

        <!-- 分页 -->
        <div class="pagination">
            第 ${page} 页
        </div>
    </div>
</div>

<script src="/js/log-viewer.js"></script>
</body>
</html>
