# Scenario 06: jQuery 传统页面反射型 XSS（jQuery）

## 业务背景

一个 2015 年前后构建的老项目后台管理系统，至今仍在使用维护。页面使用传统多页应用模式（非 SPA），依赖 jQuery 操作 DOM。

## 数据流

1. 页面从 URL 参数中读取筛选条件、搜索词等
2. `url-params.js` 解析 URL 参数并返回对象
3. `old-script.js` 获取参数后使用 jQuery 的 `.html()` 方法将内容插入页面
4. 部分渲染内容直接包含用户输入的数据

## 脆弱点

`old-script.js` 中使用 `$("#xxx").html()` 直接插入包含用户输入的 HTML。`url-params.js` 中从 URL 读取参数后未做转义。攻击者可构造 `?msg=<img src=x onerror=alert(1)>` 或 `?tab=<script>alert(1)</script>` 触发 XSS。

## 预期扫描命中行

| 文件 | 行号 | 说明 |
|------|------|------|
| `url-params.js` | 4-6 | `URLSearchParams` 或 `location.search` 读取参数 |
| `old-script.js` | 10 | `.html()` 方法调用 |
| `old-script.js` | 14 | `.append()` 方法调用 |
