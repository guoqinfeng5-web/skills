---
title: "Scenario-02: 登录错误信息回显反射型 XSS"
layout: scenario
---

## 业务背景

这是一个典型的**用户登录/认证功能**。用户在登录页面输入用户名和密码，提交到后端的 AuthController 进行身份校验。校验失败时，系统将"错误信息"通过 `request.setAttribute()` 传递到错误页面展示。

业务团队为了提供"更好的用户体验"，在错误页面中直接回显了用户输入的用户名（例如："用户 xxx 登录失败"），但使用的是 `<%= %>` 表达式，没有做 HTML 转义。

## 数据流路径

```
Browser (登录表单)
  → POST /auth/login
  → AuthController.login(@RequestParam username, password)
  → 校验失败
  → request.setAttribute("errorMsg", "用户 " + username + " 登录失败")  ← 包含未转义的用户名
  → request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward()
  → error.jsp: <%= request.getAttribute("errorMsg") %>  ← 直接输出未转义内容
```

## 脆弱点说明

### 主要漏洞点（预期扫描命中）

| 文件 | 行号范围 | 规则 ID | 说明 |
|------|----------|---------|------|
| AuthController.java | ~第20行 | JAVA-IN-002 | @RequestParam 接收用户输入 |
| AuthController.java | ~第45行 | JAVA-OUT-007 | request.setAttribute 传递未转义数据 |
| error.jsp | ~第20行 | JAVA-OUT-001 | <%= request.getAttribute("errorMsg") %> 直接输出 |
| error.jsp | ~第15行 | JAVA-OUT-001 | <%= request.getAttribute("loginName") %> 直接输出 |

### 为什么这是漏洞

1. 用户名是用户可控的输入，`<script>alert(document.cookie)</script>` 可以作为一个用户名提交
2. `AuthController` 在校验失败后，将用户名直接拼接到错误消息中，通过 `request.setAttribute` 传递
3. `error.jsp` 使用 `<%= request.getAttribute("errorMsg") %>` 直接输出内容，不做任何转义
4. JSP 中的 `<%= %>` 表达式是**不自动转义**的，恶意脚本将在受害者浏览器中执行
5. 即使是 `EL 表达式` ${}（在 JSP 2.0+ 中默认转义），也需要确认配置，但 `<%= %>` 是**绝对不会转义**的

### 修复建议

- 将 `<%= request.getAttribute("errorMsg") %>` 改为使用 EL + JSTL：`<c:out value="${errorMsg}" />`
- 或在 Java 代码中使用 `HtmlUtils.htmlEscape()` 对错误消息进行编码后再 setAttribute
- 注意：`<%= %>` 是 scriptlet 表达式，无论配置如何都不会自动转义
