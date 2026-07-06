# Scenario 04: JSONP XSS — 安全修复版

## 修复概述

修复 JSONP 接口中 callback 参数未校验导致的反射型 XSS：对 callback 参数进行严格的正则校验，推荐使用 CORS 替代 JSONP。

## 修复前后对比

| 方面 | 脆弱版本 | 安全修复版 |
|------|----------|------------|
| callback 校验 | 无校验，直接拼接输出 | 正则 `^[a-zA-Z_][a-zA-Z0-9_.]*$` 严格校验 |
| 无效 callback | 照常输出执行 | 返回错误信息，不执行任何 JS |
| 数据获取方式 | 仅 JSONP | 推荐 CORS + fetch API 替代 |
| DOM 操作 | 可能使用 innerHTML | 使用 textContent + createElement |

## 修复详解

### 1. Java 后端校验（jsonp.jsp 第 5 行）

```java
String callback = request.getParameter("callback");
if (callback == null || !callback.matches("^[a-zA-Z_][a-zA-Z0-9_.]*$")) {
    out.println("/** 错误：无效的 callback 参数 */");
    return;
}
```

只允许字母、数字、下划线和点号组成的合法 JavaScript 函数名，拒绝包含括号、引号、尖括号等特殊字符的 payload。例如 `?callback=alert(1)` 会因为包含括号无法通过校验。

### 2. 前端客户端校验（jsonpCallback.jsp 第 14-16 行）

```js
function isValidCallback(name) {
  return /^[a-zA-Z_][a-zA-Z0-9_.]*$/.test(name);
}
```

在创建 script 标签前也对 callback 进行校验，形成前后端双重防护。

### 3. 推荐 CORS 替代方案（legacy-page.html）

使用 `fetch` API + CORS 获取数据，完全避免 JSONP 的安全风险。使用 `textContent` 和 `createElement` 安全构建 DOM。

## 为什么修复有效

1. **严格的 callback 校验**：恶意 payload 如 `alert(1)` 包含括号，无法通过正则校验
2. **前后端双重校验**：即使后端校验被绕过，前端也有第二道防线
3. **CORS 替代 JSONP**：从根本上避免 callback 注入问题，`fetch` 返回的是纯 JSON 数据

## 预期扫描不再告警

| 文件 | 行号 | 修复说明 |
|------|------|----------|
| `jsonp.jsp` | 5-6 | 添加 callback 正则校验 |
| `jsonpCallback.jsp` | 14-16 | 前端校验 callback 合法性 |
| `legacy-page.html` | 32, 40 | 使用 textContent / createElement 安全渲染 |
