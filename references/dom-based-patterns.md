# 前端 DOM 型 XSS 检测模式

## 什么是 DOM 型 XSS

DOM 型 XSS 与反射型/存储型的核心区别：**攻击 payload 不需要到达服务器，完全在浏览器端完成。**

```
反射型: URL参数 → 服务器 → HTML响应 → 浏览器渲染
存储型: 输入 → 服务器 → DB存储 → 请求 → 服务器 → HTML响应 → 浏览器渲染
DOM 型: 浏览器 API(source) → JS 处理 → DOM API(sink)
```

## 一、典型数据流模式

### 模式 1：URL fragment → innerHTML

```
URL: https://site.com/page#<img src=x onerror=alert(1)>

  → location.hash                        ← DOM source
    → 字符串截取/解码
      → element.innerHTML = hashContent   ← DOM sink
```

这是最常见的 DOM 型 XSS 模式。hash 部分（# 后面）不会被发送到服务器，因此服务器无法防御。

### 模式 2：fetch 响应 → innerHTML（response-based DOM XSS）

```javascript
fetch("/api/data")
  .then(r => r.text())
  .then(data => {
    document.getElementById("result").innerHTML = data;  // ← 服务器返回的安全数据在此处被不安全使用
  });
```

这个场景容易被误判为反射型 XSS——虽然数据来自服务器，但风险发生在**浏览器端的使用方式**上，服务器返回合法 JSON 但前端未正确使用。

### 模式 3：postMessage → DOM

```
parent window
  → postMessage({type: "notification", html: "<script>..."}, "*")   ← targetOrigin 为 "*"
    → iframe 接收
      → container.innerHTML = event.data.html                       ← DOM sink
```

两个风险点：① postMessage 未限制 targetOrigin；② 接收方未校验 origin 和 data 结构。

### 模式 4：eval / setTimeout 字符串执行

```javascript
// 从 URL 获取表达式
const expr = new URLSearchParams(location.search).get("expr");
eval(expr);                           // ← 最危险的代码执行

// 从 hash 获取回调名
const cb = location.hash.slice(1);
setTimeout(cb, 100);                  // ← 字符串参数执行

// 从 URL 获取数据并构造 Function
const formula = params.get("calc");
new Function("return " + formula);    // ← 动态函数构造
```

### 模式 5：location.href 赋值跳转

```javascript
const target = new URLSearchParams(location.search).get("url");
location.href = target;               // ← 可注入 javascript:alert(1)
```

### 模式 6：属性注入（setAttribute）

```javascript
const imgSrc = params.get("src");
img.setAttribute("src", imgSrc);        // ← 可注入 javascript: 协议

const onError = params.get("onerror");
img.setAttribute("onerror", onError);   // ← 可直接注入事件处理器
```

### 模式 7：CSS 注入（数据窃取）

```javascript
const color = params.get("color");
element.style.backgroundImage = "url(" + color + ")";  // ← CSS 注入
element.style.cssText = userInput;                     // ← 完整的 CSS 属性注入
```

CSS 注入可被用于窃取 CSRF token（通过 background-image URL 带出数据）。

### 模式 8：hash / history 路由 → 解码 → 渲染

```javascript
// SPA hash 路由
const hash = location.hash.replace("#/content?name=", "");
const decoded = decodeURIComponent(hash);
document.getElementById("output").innerHTML = decoded;

// history API
const stateData = history.state?.content;
element.innerHTML = stateData;
```

---

## 二、DOM 型 XSS 检测策略

### 追踪方法：纯客户端数据流

1. 标记所有 **source**：`location.*`、`document.*`、`window.*`、`URLSearchParams`、`history.*`
2. 标记所有 **sink**：`innerHTML`、`document.write`、`eval`、`.html()`、`v-html`、`setAttribute`
3. 在 JS 文件范围内追踪：source → 中间变量 → sink

### 上下文类型

| 上下文 | 编码方式 | 常见 sink |
|--------|---------|----------|
| HTML 上下文 | HTML 实体编码 | innerHTML, outerHTML, insertAdjacentHTML |
| 属性上下文 | HTML 属性编码 | setAttribute(name, val), element.src = val |
| URL 上下文 | URL 编码/协议校验 | location.href, element.src, element.href |
| JS 上下文 | Unicode 转义 | eval, setTimeout(str), new Function |
| CSS 上下文 | CSS 转义 | element.style.*, cssText |

---

## 三、常见安全误区

| 误区 | 说明 |
|------|------|
| "数据来自服务器，服务器已经安全了" | response-based DOM XSS 中服务器返回安全内容，但前端用 innerHTML 渲染 |
| "我用的是前端框架(Vue/React)不会 XSS" | React `dangerouslySetInnerHTML` 和 Vue `v-html` 都会绕过框架的自动转义 |
| "只用了 location.search 应该没问题" | location.search 的数据来自 URL，可被攻击者控制 |
| "postMessage 只收自己的消息" | 没有校验 origin 和 source 就可以被任意 iframe 伪造 |
| "用了 encodeURI 就够了" | encodeURI 不编码 HTML 特殊字符（<, >, &），需要 encodeURIComponent |
| "hash 后面的内容不会到服务器所以安全" | hash 内容是 DOM 型 XSS 最常见的攻击入口 |
| "eval 只是用来解析 JSON" | JSON.parse 才是 JSON 解析的正确方式，eval 可执行任意代码 |
| "setTimeout('...', 100) 只是为了兼容" | setTimeout 字符串参数和 eval 一样是代码执行 |
