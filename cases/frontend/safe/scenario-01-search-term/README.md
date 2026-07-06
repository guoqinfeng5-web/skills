# Scenario 01: 搜索词反射型 XSS — 安全修复版

## 修复概述

修复 `SearchResult.jsx` 中的 XSS 漏洞，同时保留搜索结果高亮功能。

## 修复前后对比

| 方面 | 脆弱版本 | 安全修复版 |
|------|----------|------------|
| 渲染方式 | `dangerouslySetInnerHTML` 直接渲染拼接的 HTML | 使用 `DOMPurify.sanitize()` 消毒后渲染 |
| 正则构建 | 直接使用用户输入构建正则 `new RegExp(\`(${term})\`)` | 对 term 中特殊正则字符进行转义，防止 ReDoS |
| 搜索词传参 | `searchApi.ts` 已使用 `encodeURIComponent` | 保持不变（本来就是安全的） |

## 修复详解

### 1. DOMPurify 消毒（SearchResult.jsx 第 7 行）

**修复前：**
```jsx
<p dangerouslySetInnerHTML={{ __html: highlightText(item.title, searchTerm) }} />
```

**修复后：**
```jsx
<p dangerouslySetInnerHTML={{ __html: DOMPurify.sanitize(highlightText(item.title, searchTerm)) }} />
```

`DOMPurify.sanitize()` 会移除所有危险标签和属性（如 `<script>`、`onerror` 等），只保留安全的 HTML 标签（如 `<mark>`），从根本上防止 XSS 注入。

### 2. 正则表达式转义（SearchResult.jsx 第 6 行）

**修复前：**
```js
const regex = new RegExp(`(${term})`, "gi");
```

**修复后：**
```js
const regex = new RegExp(`(${term.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")})`, "gi");
```

对用户输入的搜索词中的正则特殊字符进行转义，防止恶意构造的正则表达式导致 ReDoS 攻击或意外匹配。

## 为什么修复有效

1. **输入即不可信**：URL 参数 `q` 是用户可控的输入，始终视为恶意内容
2. **消毒优先**：即使需要渲染 HTML，也必须经过 DOMPurify 等安全库的处理
3. **纵深防御**：正则转义防止了另一种攻击向量（ReDoS），形成了多层防护

## 预期扫描不再告警

| 文件 | 行号 | 修复说明 |
|------|------|----------|
| `SearchResult.jsx` | 18,22 | `dangerouslySetInnerHTML` 仍在用，但内容已通过 DOMPurify 消毒 |
