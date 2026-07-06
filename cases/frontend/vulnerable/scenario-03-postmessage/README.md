# Scenario 03: postMessage 反射型 XSS（原生 JS）

## 业务背景

支付成功页面中嵌入了一个 iframe 来展示"支付结果通知"。父页面通过 postMessage 向 iframe 发送用户姓名和支付金额等信息。

## 数据流

1. 父页面通过 `postMessage()` 向子 iframe 发送 `{ type: "payment", userName: "...", amount: "..." }`
2. `iframeReceiver.js` 注册 `message` 事件监听
3. 消息数据经过 `messageHandler.js` 格式化处理
4. 格式化后的 HTML 通过 `innerHTML` 插入 DOM

## 脆弱点

`messageHandler.js` 中用模板字符串拼接 HTML 后返回到 `iframeReceiver.js`，`iframeReceiver.js` 直接赋值给 `innerHTML`。如果父页面被恶意站点嵌入或通过其他方式发送构造消息，可植入 `<img src=x onerror=alert(1)>`。

## 预期扫描命中行

| 文件 | 行号 | 说明 |
|------|------|------|
| `iframeReceiver.js` | 6 | `addEventListener("message", ...)` postMessage 监听 |
| `iframeReceiver.js` | 12 | `innerHTML` 赋值渲染 |
| `messageHandler.js` | 7 | 模板字符串拼接包含用户输入 |
