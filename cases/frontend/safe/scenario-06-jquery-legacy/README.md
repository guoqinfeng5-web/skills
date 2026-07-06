# Scenario 06: jQuery 遗留代码 XSS — 安全修复版

## 修复概述

修复 jQuery 遗留代码中使用 `.html()` 方法导致的 XSS 漏洞：全部改用 `.text()` 方法或原生 `createTextNode()` 设置内容。

## 修复前后对比

| 方面 | 脆弱版本 | 安全修复版 |
|------|----------|------------|
| 内容设置 | `.html()` 直接插入 HTML 字符串 | `.text()` 自动转义 HTML 字符 |
| DOM 构建 | 拼接 HTML 字符串 | 使用 jQuery 链式调用 + `createTextNode` |
| URL 参数读取 | 直接拼接 | 使用 `URLSearchParams` 标准 API |
| 富文本场景 | 无消毒 | 正则白名单消毒（仅允许 b/i/em） |

## 修复详解

### 1. .text() 替代 .html()（old-script.js 第 13-17 行）

**修复前：**
```js
$("<strong>").html(comment.author).appendTo($div);
$("<p>").html(comment.content).appendTo($div);
```

**修复后：**
```js
$("<strong>").text(comment.author).appendTo($div);
$("<p>").text(comment.content).appendTo($div);
```

jQuery 的 `.text()` 方法使用 `textContent` 设置内容，自动转义所有 HTML 标签。

### 2. createTextNode 替代 innerHTML（old-script.js 第 15 行）

```js
$div.append(document.createTextNode(" 说："));
```

使用原生 `document.createTextNode()` 确保不会执行任何 HTML。

### 3. URLSearchParams 替代手动解析（url-params.js 第 7-9 行）

```js
function getQueryParam(name) {
  var params = new URLSearchParams(window.location.search);
  return params.get(name) || "";
}
```

使用标准 API 安全读取 URL 参数，避免手动解析带来的编码问题。

### 4. 富文本白名单消毒（url-params.js 第 30-36 行）

```js
var sanitized = decoded
  .replace(/</g, "&lt;")
  .replace(/>/g, "&gt;")
  .replace(/&lt;(\/?(?:b|i|em))&gt;/gi, "<$1>");
```

先整体转义，再恢复白名单标签，确保只允许 `<b>`、`<i>`、`<em>` 标签。

## 为什么修复有效

1. **.text() 自动转义**：jQuery 的 `.text()` 方法内部使用 `textContent`，安全无副作用
2. **标准化 API**：`URLSearchParams` 提供安全的参数解析
3. **白名单策略**：即使需要富文本，也限制在最小必要标签范围内

## 预期扫描不再告警

| 文件 | 行号 | 修复说明 |
|------|------|----------|
| `old-script.js` | 14-17 | 使用 `.text()` 替代 `.html()` |
| `url-params.js` | 13, 22 | 使用 `.text()` 安全填充 URL 参数内容 |
