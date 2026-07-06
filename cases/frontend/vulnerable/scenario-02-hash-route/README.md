# Scenario 02: Hash 路由反射型 XSS（Vue）

## 业务背景

一个单页应用（SPA）使用 Vue Router 的 hash 模式。页面通过 hash 路由的 query 参数（如 `#/content?name=xxx`）来渲染动态内容。

## 数据流

1. 用户访问 `https://app.example.com/#/content?name=张三`
2. Vue Router 解析 hash 路由 → `RouterView.vue` 匹配路由
3. `ContentPage.vue` 通过 `route.query` 读取 `name` 参数
4. 参数经过 `hashUtils.js` 简单处理后传入模板
5. 模板中使用 `v-html` 直接渲染

## 脆弱点

`ContentPage.vue` 中从 `route.query.name` 获取用户输入后，未做转义直接传给 `v-html` 指令。攻击者可构造 `#/content?name=<script>alert(1)</script>` 触发 XSS。

## 预期扫描命中行

| 文件 | 行号 | 说明 |
|------|------|------|
| `ContentPage.vue` | 8 | `route.query` 读取 URL 参数 |
| `ContentPage.vue` | 16 | `v-html` 渲染未转义内容 |
| `hashUtils.js` | 5 | 对 query 参数做解码处理 |
