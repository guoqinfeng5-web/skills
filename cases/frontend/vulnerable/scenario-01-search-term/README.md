# Scenario 01: 搜索词反射型 XSS（React）

## 业务背景

这是一个电商平台的商品搜索功能。用户在搜索框中输入关键词后，URL 变为 `?q=关键词`，搜索结果页面会高亮显示搜索词。

## 数据流

1. 用户在搜索框输入关键词 → URL 变为 `?q=关键词`
2. `SearchComponent.jsx` 通过 `useSearchParams()` 读取 `q` 参数
3. 将搜索词传给 `SearchResult.jsx` 组件
4. `SearchResult.jsx` 调用 `searchApi.ts` 获取搜索结果
5. 前端对搜索结果中的文本高亮时，使用 `dangerouslySetInnerHTML` 渲染高亮 HTML

## 脆弱点

`SearchComponent.jsx` 中从 URL 获取的 `q` 参数直接传递给 `SearchResult`，`SearchResult` 内部使用 `dangerouslySetInnerHTML` 拼接高亮 HTML。由于没有经过转义，恶意用户可构造 `?q=<img src=x onerror=alert(1)>` 触发 XSS。

## 预期扫描命中行

| 文件 | 行号 | 说明 |
|------|------|------|
| `SearchComponent.jsx` | 10 | `useSearchParams()` 获取 URL 参数 |
| `SearchResult.jsx` | 15-16 | `dangerouslySetInnerHTML` 渲染未转义内容 |
| `searchApi.ts` | 8 | 将搜索词拼入 API 请求 |
