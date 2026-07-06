# Scenario 02: Hash 路由 XSS — 安全修复版

## 修复概述

修复 Vue 应用中通过 `v-html` 渲染 hash 参数导致的 XSS 漏洞，改用 `v-text` 指令自动转义。

## 修复前后对比

| 方面 | 脆弱版本 | 安全修复版 |
|------|----------|------------|
| 模板指令 | `v-html="content"` 直接渲染 HTML | `v-text="content"` 自动转义 |
| hash 取值 | 直接拼接字符串 | 使用 `URLSearchParams` 标准 API |
| 内容来源 | 从 hash 取值后直接渲染 | 先经过 HTML 实体转义再渲染 |

## 修复详解

### 1. v-text 替代 v-html（RouterView.vue / ContentPage.vue）

**修复前：**
```html
<div v-html="content"></div>
```

**修复后：**
```html
<div v-text="content"></div>
```

Vue 的 `v-text` 指令会使用 `textContent` 设置元素内容，自动将 `<script>`、`<img onerror>` 等 HTML 标签转义为纯文本，从根本上防止 XSS。

### 2. 安全的 hash 参数解析（hashUtils.js）

使用 `URLSearchParams` 标准 API 替代手动字符串解析，避免因特殊字符导致的解析异常或注入。

## 为什么修复有效

1. `v-text` / `{{ }}` 插值 会将所有 HTML 字符转义为实体编码
2. `URLSearchParams` 提供了标准、安全的参数解析方式
3. 即使 hash 中包含 `<img src=x onerror=alert(1)>`，也会被转义为纯文本显示

## 预期扫描不再告警

| 文件 | 行号 | 修复说明 |
|------|------|----------|
| `RouterView.vue` | 35-38 | 使用 `textContent` 转义 hash 内容 |
| `ContentPage.vue` | 5 | 使用 `v-text` 替代 `v-html` |
