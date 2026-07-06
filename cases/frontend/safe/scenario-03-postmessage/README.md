# Scenario 03: PostMessage XSS — 安全修复版

## 修复概述

修复 `postMessage` 通信中的 XSS 漏洞：添加 origin 白名单验证，使用 `textContent` 替代 `innerHTML`，并验证消息格式。

## 修复前后对比

| 方面 | 脆弱版本 | 安全修复版 |
|------|----------|------------|
| 内容渲染 | `innerHTML` 直接插入 HTML | `textContent` 安全填充纯文本 |
| Origin 验证 | 无验证，信任所有来源 | 白名单机制，仅允许受信任 origin |
| 消息格式 | 直接使用字符串 | JSON 结构解析+格式验证 |
| 数据序列化 | 无 | `JSON.parse(JSON.stringify(data))` 防止原型污染 |

## 修复详解

### 1. Origin 白名单验证（iframeReceiver.js 第 7-9 行）

```js
const ALLOWED_ORIGINS = [
  "https://trusted-parent.example.com",
  "https://www.example.com",
];
```

仅处理来自白名单中 origin 的消息，防止任意网站发送恶意 payload。

### 2. textContent 替代 innerHTML（iframeReceiver.js 第 38, 41, 44 行）

```js
output.textContent = "你好, " + (data.name || "访客") + "!";
```

`textContent` 自动转义所有 HTML 特殊字符，即使 `data.name = "<img src=x onerror=alert(1)>"`，也只会显示为纯文本。

### 3. 消息格式验证（iframeReceiver.js 第 19-27 行）

验证消息是否为有效的 JSON 对象，并检查是否包含必要的 `type` 字段，防止意外或恶意数据结构。

### 4. 明确的 targetOrigin（messageHandler.js 第 17, 28 行）

```js
iframeWindow.postMessage(safeData, targetOrigin);
```

始终指定 `targetOrigin` 而不是使用 `*`，确保消息只发送给预期的接收方。

## 为什么修复有效

1. **Origin 白名单**：从根本上阻止了来自恶意站点的消息
2. **textContent 而非 innerHTML**：无论消息内容为何，都不会执行 HTML/JavaScript
3. **输入验证**：确保消息结构符合预期格式
4. **明确的 targetOrigin**：防止消息泄露给未知窗口

## 预期扫描不再告警

| 文件 | 行号 | 修复说明 |
|------|------|----------|
| `iframeReceiver.js` | 33-44 | 使用 `textContent` 替代 `innerHTML` |
| `iframeReceiver.js` | 7-9 | 添加 origin 白名单 |
| `parent.html` | 21-26, 33-36 | 指定 targetOrigin + 验证消息 origin |
