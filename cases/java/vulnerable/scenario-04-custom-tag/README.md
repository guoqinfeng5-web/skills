---
title: "Scenario-04: 自定义 JSP 标签输出未转义用户输入的反射型 XSS"
layout: scenario
---

## 业务背景

一个**用户个人资料展示页面**。项目使用了自定义的 JSP 标签（TLD 定义）来渲染用户头像和昵称。自定义标签 `UserProfileTag` 继承自 `TagSupport`，在 `doStartTag()` 方法中直接使用 `out.print()` 输出传入的属性值。

团队认为"自定义标签封装了展示逻辑就是安全的"，但没有在标签内部做 HTML 转义，导致使用者标签时的属性值如果包含用户输入，就会产生 XSS 漏洞。

## 数据流路径

```
Browser (用户查看个人资料)
  → GET /profile?username=<script>...
  → ProfileController.viewProfile(@RequestParam username)
  → Model.addAttribute("profileUser", username)
  → profile.jsp: 使用 <u:userProfile nickName="${profileUser}" /> 自定义标签
  → UserProfileTag.doStartTag(): out.print(nickName)  ← 未转义直接输出
```

## 脆弱点说明

### 主要漏洞点（预期扫描命中）

| 文件 | 行号范围 | 规则 ID | 说明 |
|------|----------|---------|------|
| UserProfileTag.java | ~第45行 | JAVA-OUT-003 | out.print() 直接输出属性值 |
| UserProfileTag.java | ~第55行 | JAVA-OUT-003 | out.println() 直接输出属性值 |
| profile.jsp | ~第20行 | JAVA-OUT-002 | EL ${profileUser} 传递到自定义标签 |
| ProfileController.java | ~第30行 | JAVA-OUT-007 | model.addAttribute 传递未转义数据 |

### 为什么这是漏洞

1. `ProfileController` 从 URL 参数中获取 `username`，通过 `model.addAttribute` 传入视图
2. `profile.jsp` 使用自定义标签 `<u:userProfile nickName="${profileUser}" />` 渲染用户信息
3. 自定义标签 `UserProfileTag` 在 `doStartTag()` 中通过 `pageContext.getOut().print(nickName)` 直接输出
4. 自定义标签没有对传入的 `nickName` 做任何转义处理
5. 如果 `username` 包含 `<script>alert(document.cookie)</script>`，标签输出时将执行恶意脚本
6. 开发者在 JSP 中使用标签时，以为标签封装了安全的渲染逻辑，放松了警惕

### 修复建议

- 在 `UserProfileTag.doStartTag()` 中使用 `HtmlUtils.htmlEscape()` 或类似方法对属性值进行编码
- 或在 JSP 中使用 `<c:out value="${profileUser}" />` 替代自定义标签输出
- 自定义标签应当主动承担转义责任，因为调用者可能忘记转义
