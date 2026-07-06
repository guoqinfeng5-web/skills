---
title: "Scenario-03: JSON API 返回未转义 HTML 片段的反射型 XSS"
layout: scenario
---

## 业务背景

一个**用户反馈/评论系统**。前端通过 AJAX 请求提交用户评论内容，后端 REST API 处理后将评论内容以 HTML 片段的形式返回，前端通过 `innerHTML` 直接展示到页面上。

后端使用了 `@ResponseBody` 返回拼接好的 HTML 字符串而不是返回 JSON 结构化数据，前端也没有做任何安全处理（没有使用 textContent 或 DOMPurify）。

## 数据流路径

```
Browser (评论表单)
  → POST /api/feedback/submit
  → FeedbackController.submit(@RequestBody)
  → FeedbackService.processFeedback(content)  ← 未转义
  → 字符串拼接 HTML 片段 ← 漏洞
  → @ResponseBody 返回 HTML 字符串
  → feedback.js: 通过 innerHTML 展示 ← DOM-based XSS
```

## 脆弱点说明

### 主要漏洞点（预期扫描命中）

| 文件 | 行号范围 | 规则 ID | 说明 |
|------|----------|---------|------|
| FeedbackController.java | ~第27行 | JAVA-IN-004 | @RequestBody 接收用户输入 |
| FeedbackController.java | ~第52行 | JAVA-OUT-006 | @ResponseBody 返回 HTML 片段 |
| FeedbackController.java | ~第90行 | JAVA-OUT-009 | 字符串拼接构建 HTML |
| FeedbackService.java | ~第35行 | — | 未转义处理用户输入 |
| feedback.js | ~第15行 | FE-OUT-001 | innerHTML 直接赋值 |
| feedback.js | ~第35行 | FE-OUT-004 | jQuery .html() 方法 |

### 为什么这是漏洞

1. 后端通过 `@RequestBody` 获取用户提交的评论内容
2. `FeedbackService` 对评论做了"业务处理"（敏感词替换、格式化等），但**没有进行 HTML 转义**
3. `FeedbackController` 通过字符串拼接构建包含用户输入的 HTML 片段并直接返回
4. `@ResponseBody` 告诉 Spring 直接将返回值写入 HTTP 响应体，返回的是 HTML 而非 JSON
5. 前端的 `feedback.js` 通过 `innerHTML` 或 jQuery 的 `.html()` 将返回的 HTML 直接插入 DOM
6. 如果用户提交 `<img src=x onerror=alert(1)>`，将在浏览器中执行

### 修复建议

- 后端应返回 JSON 结构化数据（如 `{"content": "...", "user": "..."}`），前端的 `textContent` 渲染
- 如果后端必须返回 HTML，使用 `HtmlUtils.htmlEscape()` 对用户输入进行编码
- 前端应使用 `textContent` 替代 `innerHTML`，或使用 `DOMPurify.sanitize()` 处理后再赋值
