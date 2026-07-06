# Scenario 05: 模板字符串拼接 XSS（React）

## 业务背景

一个用户反馈/评论系统。用户可以提交文本反馈，反馈内容在列表页展示。后端返回的反馈内容包含简单 Markdown 风格的标记（**加粗**、*斜体*等），前端自行转化为 HTML。

## 数据流

1. 后端 API 返回反馈列表数据（包含用户提交的反馈内容）
2. `FeedbackList.jsx` 请求数据并渲染 `FeedbackItem` 列表
3. `FeedbackItem.jsx` 调用 `feedbackUtils.ts` 中的格式化函数将文本转为 HTML
4. 转换后的 HTML 通过 `dangerouslySetInnerHTML` 渲染

## 脆弱点

`feedbackUtils.ts` 中使用模板字符串（`）拼接 HTML，将用户输入的直接嵌入到 HTML 字符串中，未做转义。反馈提交接口未充分过滤，导致存储型 + 反射型 XSS。

## 预期扫描命中行

| 文件 | 行号 | 说明 |
|------|------|------|
| `feedbackUtils.ts` | 8-12 | 模板字符串拼接包含用户输入的 HTML |
| `FeedbackItem.jsx` | 12 | `dangerouslySetInnerHTML` 渲染 |
| `feedbackUtils.ts` | 4 | 正则替换操作 |
