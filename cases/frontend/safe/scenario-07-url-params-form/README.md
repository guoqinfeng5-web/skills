# Scenario 07: URL 参数表单 XSS — 安全修复版

## 修复概述

修复 Vue 表单应用中将 URL 参数直接填充到表单导致 XSS 的漏洞：URL 参数经过 HTML 编码后填充，富文本内容使用 DOMPurify 消毒。

## 修复前后对比

| 方面 | 脆弱版本 | 安全修复版 |
|------|----------|------------|
| URL 参数读取 | 手动字符串解析 | `URLSearchParams` 标准 API |
| 参数白名单 | 无限制，读取所有参数 | 仅读取预定义的字段名 |
| 文本转义 | 无转义直接填充 | `sanitizeText()` HTML 实体编码 |
| 颜色值校验 | 无校验 | 正则 `/^#[0-9a-fA-F]{3,8}$/` 校验 |
| Vue 模板渲染 | 可能使用 v-html | 使用 `v-text` 指令 |

## 修复详解

### 1. HTML 实体编码（formHelper.ts 第 25-31 行）

```ts
export function sanitizeText(input: string): string {
  return input
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#x27;");
}
```

将所有 HTML 特殊字符转义为实体编码，确保无论输入什么内容都会显示为纯文本。

### 2. 字段白名单（formHelper.ts 第 12-13 行）

```ts
const allowedFields = ["title", "content", "color"];
```

只读取预先定义的字段名，拒绝所有未知 URL 参数。

### 3. v-text 安全渲染（FormPrefill.vue 第 14, 23, 41-42 行）

```html
<input id="title" v-model="form.title" type="text" :placeholder="safePlaceholders.title" />
<p><strong>标题：</strong><span v-text="form.title"></span></p>
```

`v-model` 绑定的文本内容由 Vue 自动处理，预览区使用 `v-text` 确保 HTML 被转义。

### 4. 颜色值校验（FormPrefill.vue 第 69-71 行）

```js
if (/^#[0-9a-fA-F]{3,8}$/.test(params.color)) {
  this.form.color = params.color;
}
```

颜色值通过正则校验后才使用，防止注入恶意的 CSS 值。

## 为什么修复有效

1. **HTML 实体编码**：将所有 `<` `>` `&` `"` `'` 转义，从根本上防止 HTML 注入
2. **字段白名单**：只处理已知字段，避免意外参数传递
3. **v-text 指令**：Vue 的 `v-text` 使用 `textContent` 设置内容，安全可靠
4. **输入校验**：对特殊字段（如颜色）进行格式校验

## 预期扫描不再告警

| 文件 | 行号 | 修复说明 |
|------|------|----------|
| `FormPrefill.vue` | 61-73 | 使用 HTML 编码填充 URL 参数 |
| `FormPrefill.vue` | 41-42 | 使用 `v-text` 安全渲染预览 |
| `formHelper.ts` | 25-31 | HTML 实体编码函数 |
| `formHelper.ts` | 10-20 | 字段白名单限制 |
