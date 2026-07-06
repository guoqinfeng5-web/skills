---
title: "Scenario-05: 文件上传后文件名回显的反射型 XSS"
layout: scenario
---

## 业务背景

一个**文件上传功能**，用户上传文件后，页面会显示"xxx 文件上传成功"的消息。上传的文件名（`getOriginalFilename()`）未经任何转义直接回显到页面上。

攻击者可以构造一个包含恶意脚本的文件名（如 `<script>alert(1)</script>.txt`），上传后文件名在页面上渲染时触发 XSS。

## 数据流路径

```
Browser (上传文件，文件名包含恶意脚本)
  → POST /file/upload (multipart/form-data)
  → FileUploadController.upload(@RequestParam MultipartFile file)
  → file.getOriginalFilename() ← 获取原始文件名（用户可控）
  → FileService.storeFile(file) ← 存储文件
  → model.addAttribute("filename", originalFilename) ← 未转义
  → uploadResult.jsp: ${filename} ← 直接输出
```

## 脆弱点说明

### 主要漏洞点（预期扫描命中）

| 文件 | 行号范围 | 规则 ID | 说明 |
|------|----------|---------|------|
| FileUploadController.java | ~第43行 | JAVA-IN-008 | MultipartFile 接收文件输入 |
| FileUploadController.java | ~第50行 | JAVA-IN-008 | getOriginalFilename() 获取原始文件名 |
| FileUploadController.java | ~第52行 | JAVA-OUT-007 | model.addAttribute 传递未转义数据 |
| FileUploadController.java | ~第60行 | JAVA-OUT-002 | EL ${filename} 直接输出到 JSP |
| uploadResult.jsp | ~第20行 | JAVA-OUT-002 | EL 表达式直接输出文件名 |

### 为什么这是漏洞

1. 攻击者可以使用包含 HTML/JS 的文件名上传文件，如 `<img src=x onerror=alert(1)>.jpg`
2. `FileUploadController` 通过 `getOriginalFilename()` 获取客户端提交的原始文件名
3. 文件名未经任何转义直接通过 `model.addAttribute` 传递到视图
4. `uploadResult.jsp` 使用 EL 表达式 `${filename}` 直接输出文件名内容
5. 如果 JSP 没有启用 `isELIgnored` 或 EL 默认转义配置，恶意脚本将执行
6. 即使 JSP 2.0+ 默认部分转义 EL 输出，仍然依赖具体配置，且 `${}` 被 JAVA-OUT-002 规则标记

### 修复建议

- 在 Controller 中使用 `HtmlUtils.htmlEscape()` 对文件名进行编码
- 在 JSP 中使用 `<c:out value="${filename}" />` 替代 `${filename}`
- 对上传文件进行重命名，避免使用原始文件名
