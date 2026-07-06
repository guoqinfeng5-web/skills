---
title: "Scenario-06: 重定向 URL 参数拼接的反射型 XSS"
layout: scenario
---

## 业务背景

一个**登录后自动跳转**的功能。用户在未登录状态下访问受保护页面时，系统将当前页面 URL 编码到参数中，登录完成后自动跳转回之前的页面。但跳转 URL 是由用户传入的参数决定的，且没有做严格的 URL 编码和校验。

攻击者可以构造一个包含 `javascript:` 伪协议或包含恶意脚本的 URL，利用重定向机制实施反射型 XSS 攻击。

## 数据流路径

```
攻击者构造恶意链接:
  → /auth/login?redirect=javascript:alert(document.cookie)
  或
  → /auth/login?redirect=http://evil.com/page.html#<script>...

用户访问恶意链接并登录:
  → LoginController.login(@RequestParam redirect)
  → "redirect:" + redirectUrl  ← 直接拼接
  → 或 response.sendRedirect(redirectUrl)  ← 未校验
  
浏览器跳转到恶意 URL → XSS 执行
```

## 脆弱点说明

### 主要漏洞点（预期扫描命中）

| 文件 | 行号范围 | 规则 ID | 说明 |
|------|----------|---------|------|
| LoginController.java | ~第27行 | JAVA-IN-002 | @RequestParam 接收 redirect 参数 |
| LoginController.java | ~第85行 | JAVA-OUT-010 | "redirect:" + redirectUrl 字符串拼接 |
| RedirectController.java | ~第35行 | JAVA-IN-002 | @RequestParam 接收 redirect 参数 |
| RedirectController.java | ~第45行 | JAVA-OUT-010 | response.sendRedirect(redirectUrl) 调用 |
| login.jsp | ~第30行 | JAVA-OUT-002 | EL ${redirect} 在隐藏表单中回显 |

### 为什么这是漏洞

1. `redirect` 参数由用户直接传入，未做任何 URL 编码和白名单校验
2. `LoginController` 中通过 `"redirect:" + redirectUrl` 直接拼接重定向 URL
3. Spring 的 `redirect:` 前缀会触发视图解析器执行重定向，如果拼接了恶意 `javascript:` 伪协议，可以在某些浏览器中执行脚本
4. `RedirectController` 中使用了 `response.sendRedirect(redirectUrl)`，同样没有校验
5. `login.jsp` 中通过 `${redirect}` 在隐藏表单中回显了 redirect 参数，如果包含恶意字符也会触发 XSS

### 修复建议

- 使用 `UriComponentsBuilder` 构建 URL 并进行编码
- 对重定向 URL 做白名单校验，只允许跳转到本站域名
- 使用 Spring 的 `RedirectAttributes` 代替手动拼接
- 对 redirect 参数进行 URL 编码
