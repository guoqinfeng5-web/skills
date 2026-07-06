<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>XSS 反射型测试项目</title>
    <link rel="stylesheet" href="/css/common.css"/>
</head>
<body>
<div class="container">
    <h1>反射型 XSS 测试项目</h1>
    <p class="subtitle">本工程<strong>故意包含</strong>多种反射型 XSS 漏洞场景，供外部 XSS Skill 静态扫描与修复建议验证。</p>

    <section class="highlight-box">
        <h2>推荐：跨模块联动入口</h2>
        <p>以下入口串联多个场景，Skill 需跨文件追踪数据流（比单场景更接近真实项目）：</p>
        <ul>
            <li><a href="/portal/dashboard"><strong>/portal/dashboard</strong></a> — JSP 门户：登录 session → 搜索 → 审计日志 → 自定义 Tag</li>
            <li><a href="/frontend/portal/index.html?q=test&user=admin"><strong>/frontend/portal/</strong></a> — 前后端分离：Portal API + Feedback API + Log API + jQuery</li>
        </ul>
        <p>联动数据流示例：</p>
        <pre>用户输入 q → SearchService → BusinessWrapper → dashboard.jsp EL
              ↘ AuditTrailService → LogService → admin.jsp / AJAX innerHTML
登录 username → AuthController → AuditTrailService → 日志二次展示
反馈 content → FeedbackService → AuditTrail + innerHTML</pre>
    </section>

    <section>
        <h2>Java 后端场景（Spring MVC + JSP）</h2>
        <table class="scenario-table">
            <thead>
                <tr><th>编号</th><th>场景</th><th>入口</th><th>漏洞模式</th></tr>
            </thead>
            <tbody>
                <tr>
                    <td>01</td>
                    <td>多层参数透传</td>
                    <td><a href="/scenario01/search?q=test">/scenario01/search?q=</a></td>
                    <td>Controller→Service→Wrapper→JSP EL</td>
                </tr>
                <tr>
                    <td>02</td>
                    <td>错误信息回显</td>
                    <td><a href="/scenario02/auth/login">/scenario02/auth/login</a> (POST)</td>
                    <td>username→errorMsg→JSP &lt;%= %&gt;</td>
                </tr>
                <tr>
                    <td>03</td>
                    <td>JSON 转 HTML API</td>
                    <td><a href="/scenario03/feedback">/scenario03/feedback</a></td>
                    <td>@ResponseBody HTML + innerHTML</td>
                </tr>
                <tr>
                    <td>04</td>
                    <td>自定义 JSP 标签</td>
                    <td><a href="/scenario04/profile?username=guest">/scenario04/profile</a></td>
                    <td>UserProfileTag out.print()</td>
                </tr>
                <tr>
                    <td>05</td>
                    <td>文件上传回显</td>
                    <td><a href="/scenario05/file/upload">/scenario05/file/upload</a></td>
                    <td>getOriginalFilename()→EL</td>
                </tr>
                <tr>
                    <td>06</td>
                    <td>重定向拼接</td>
                    <td><a href="/scenario06/auth/login?redirect=/">/scenario06/auth/login</a></td>
                    <td>redirect: 拼接 + sendRedirect</td>
                </tr>
                <tr>
                    <td>07</td>
                    <td>日志二次反射</td>
                    <td><a href="/scenario07/admin/logs">/scenario07/admin/logs</a></td>
                    <td>存储→JSP EL + AJAX innerHTML</td>
                </tr>
            </tbody>
        </table>
    </section>

    <section>
        <h2>前端场景（静态页面）</h2>
        <table class="scenario-table">
            <thead>
                <tr><th>编号</th><th>场景</th><th>入口</th><th>漏洞模式</th></tr>
            </thead>
            <tbody>
                <tr>
                    <td>F01</td>
                    <td>搜索词高亮</td>
                    <td><a href="/frontend/scenario01-search/index.html?q=test">/frontend/scenario01-search/</a></td>
                    <td>URL→innerHTML 高亮</td>
                </tr>
                <tr>
                    <td>F02</td>
                    <td>Hash 路由</td>
                    <td><a href="/frontend/scenario02-hash/index.html#/content?name=test">/frontend/scenario02-hash/</a></td>
                    <td>hash→v-html</td>
                </tr>
                <tr>
                    <td>F03</td>
                    <td>postMessage</td>
                    <td><a href="/frontend/scenario03-postmessage/parent.html?user=test">/frontend/scenario03-postmessage/</a></td>
                    <td>postMessage→innerHTML</td>
                </tr>
                <tr>
                    <td>F04</td>
                    <td>JSONP callback</td>
                    <td><a href="/frontend/scenario04-jsonp/legacy-page.html?cb=test">/frontend/scenario04-jsonp/</a></td>
                    <td>callback 参数注入</td>
                </tr>
                <tr>
                    <td>F05</td>
                    <td>模板字符串</td>
                    <td><a href="/frontend/scenario05-template/index.html">/frontend/scenario05-template/</a></td>
                    <td>模板拼接→innerHTML</td>
                </tr>
                <tr>
                    <td>F06</td>
                    <td>jQuery 遗留</td>
                    <td><a href="/frontend/scenario06-jquery/legacyPage.html?user=test">/frontend/scenario06-jquery/</a></td>
                    <td>URL→jQuery .html()</td>
                </tr>
                <tr>
                    <td>F07</td>
                    <td>URL 表单预填</td>
                    <td><a href="/frontend/scenario07-form/edit-page.html">/frontend/scenario07-form/</a></td>
                    <td>base64 URL→v-html</td>
                </tr>
            </tbody>
        </table>
    </section>

    <section class="warning">
        <p>⚠️ 仅供安全测试与 Skill 扫描验证，请勿部署到生产环境。</p>
    </section>
</div>
</body>
</html>
