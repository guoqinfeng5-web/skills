# Scenario 07: URL 参数表单预填充 XSS（Vue）

## 业务背景

一个内容管理系统的编辑页面。用户从列表页点击"编辑"时，URL 中包含编辑内容的初始数据（base64 编码的富文本内容），前端自动解码并填充到富文本编辑器中。

## 数据流

1. 列表页点击编辑 → 跳转到 `edit-page.html#/edit?content=PGJyPjxzY3JpcHQ+YWxlcnQoMSk8L3NjcmlwdD4=`
2. Vue 应用加载，路由解析 query 参数
3. `formHelper.ts` 读取并解码 URL 参数
4. `FormPrefill.vue` 使用解码后的内容并通过 `v-html` 渲染到富文本预览区

## 脆弱点

`formHelper.ts` 中解码 URL 参数后直接返回，`FormPrefill.vue` 将解码后的内容直接通过 `v-html` 渲染。攻击者可构造 base64 编码的恶意 HTML 触发 XSS。

## 预期扫描命中行

| 文件 | 行号 | 说明 |
|------|------|------|
| `formHelper.ts` | 8-10 | 读取 URL 参数并解码 |
| `FormPrefill.vue` | 8 | `v-html` 渲染解码后内容 |
| `formHelper.ts` | 5 | `atob()` 解码操作 |
