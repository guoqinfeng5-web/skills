# Scenario 05: 模板字面量 XSS — 安全修复版

## 修复概述

修复 React 组件中通过模板字面量拼接 HTML 并直接渲染导致的 XSS 漏洞：使用 JSX 默认 {} 插值自动转义，富文本场景使用 DOMPurify 消毒。

## 修复前后对比

| 方面 | 脆弱版本 | 安全修复版 |
|------|----------|------------|
| 文本渲染 | `dangerouslySetInnerHTML` + 模板字面量 | JSX `{}` 插值 自动转义 |
| 富文本渲染 | 无消毒直接渲染 | `DOMPurify.sanitize()` 允许标签白名单 |
| 数据处理 | 无校验 | 使用 TypeScript 类型接口约束 |
| API 调用 | 无安全处理 | 响应状态码校验 + JSON 序列化 |

## 修复详解

### 1. JSX 默认转义（FeedbackItem.jsx 第 6-8 行）

```jsx
const renderTextContent = () => {
  return <p>{feedback.content}</p>;
};
```

React 在 `{}` 插值中会自动对字符串进行 HTML 实体转义，`<script>alert(1)</script>` 会显示为纯文本。

### 2. 受控的富文本渲染（FeedbackItem.jsx 第 11-16 行）

```jsx
const sanitized = DOMPurify.sanitize(feedback.content, {
  ALLOWED_TAGS: ["b", "i", "em", "strong", "a", "p", "br"],
  ALLOWED_ATTR: ["href"],
});
return <p dangerouslySetInnerHTML={{ __html: sanitized }} />;
```

只有用户明确选择允许 HTML 时才渲染富文本，且经过 DOMPurify 白名单过滤。

### 3. 类型安全（feedbackUtils.ts）

使用 TypeScript 接口定义数据结构，确保数据处理的一致性和可预测性。

## 为什么修复有效

1. **JSX 默认转义**：React 的 `{}` 插值自动转义 HTML 字符，是天然的安全屏障
2. **纵深防御**：即使 `dangerouslySetInnerHTML` 仍在使用，内容已通过 DOMPurify 消毒
3. **最小权限原则**：默认使用纯文本渲染，富文本需要显式选择

## 预期扫描不再告警

| 文件 | 行号 | 修复说明 |
|------|------|----------|
| `FeedbackItem.jsx` | 7 | 使用 `{}` 插值自动转义 |
| `FeedbackItem.jsx` | 12-14 | DOMPurify 白名单过滤富文本 |
| `FeedbackList.jsx` | 全部 | 无未转义渲染 |
