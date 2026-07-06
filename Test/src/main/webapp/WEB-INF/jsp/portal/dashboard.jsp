<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/tags/userProfile.tld" prefix="u" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>企业门户 - 联动场景</title>
    <link rel="stylesheet" href="/css/common.css"/>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
</head>
<body>
<div class="container wide">
    <h1>企业门户 <span class="tag">跨模块联动</span></h1>
    <p class="subtitle">
        本页串联登录(session) → 搜索(scenario01) → 审计日志(scenario07) → 用户资料(scenario04)。
        Skill 需跨包追踪完整数据流。
    </p>

    <nav class="portal-nav">
        <a href="/scenario02/auth/login">登录</a>
        <a href="/scenario03/feedback">反馈</a>
        <a href="/scenario05/file/upload">上传</a>
        <a href="/scenario07/admin/logs">日志后台</a>
        <a href="/frontend/portal/index.html">前后端分离门户</a>
    </nav>

    <!-- welcomeHtml 由 PortalService 拼接 username + lastSearch，未经转义 -->
    <div class="welcome-section">
        ${welcomeHtml}
    </div>

    <div class="portal-grid">
        <section class="portal-panel">
            <h2>用户资料 <small>(scenario04 自定义 Tag)</small></h2>
            <u:userProfile nickName="${username}" level="${userLevel}" avatar="/images/default.png"/>
            <p>简介: ${userBio}</p>
        </section>

        <section class="portal-panel">
            <h2>搜索 <small>(scenario01 多层透传)</small></h2>
            <form action="/portal/dashboard" method="get">
                <input type="text" name="q" value="${lastSearch}" placeholder="搜索商品"/>
                <button type="submit">搜索</button>
            </form>
            <c:if test="${not empty searchResult}">
                <div class="search-output">
                    ${searchResult.displayContent}
                </div>
                <p>建议: ${searchResult.suggestion}</p>
            </c:if>
            <div id="ajaxSearchResult"></div>
            <button id="ajaxSearchBtn" type="button">AJAX 搜索（调用 Portal API）</button>
        </section>
    </div>

    <section class="portal-panel">
        <h2>最近操作日志 <small>(scenario07 二次反射)</small></h2>
        <p>以下日志由搜索/登录/反馈/上传经 <code>AuditTrailService</code> 写入</p>
        <table class="log-table">
            <thead>
                <tr><th>操作</th><th>用户</th><th>消息</th><th>详情</th><th>时间</th></tr>
            </thead>
            <tbody>
                <c:forEach items="${recentLogs}" var="log">
                    <tr>
                        <td>${log.action}</td>
                        <td>${log.user}</td>
                        <td class="log-message">${log.message}</td>
                        <td class="log-detail">${log.detail}</td>
                        <td>${log.formattedTime}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </section>
</div>
<script src="/js/portal-dashboard.js"></script>
</body>
</html>
