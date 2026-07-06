# 前端反射型 XSS 检测模式

## 一、输入源（Input Sources）

### 1. 浏览器 API

| API | 说明 | 典型场景 |
|-----|------|---------|
| `location.search` | URL 查询字符串 | 搜索页、列表页参数 |
| `location.hash` | URL 片段标识 | SPA 路由、锚点导航 |
| `location.href` | 完整 URL | URL 参数提取 |
| `location.pathname` | URL 路径 | 路径参数提取 |
| `document.URL` | 文档 URL | 兼容性代码 |
| `document.documentURI` | 文档 URI | 较新 API |
| `document.referrer` | 来源页面 URL | 来源跟踪 |
| `window.name` | 窗口名称 | 跨窗口通信（旧手法） |
| `URLSearchParams` | URL 参数解析 | 现代浏览器的标准方式 |

### 2. 前端框架路由

| 框架 | API | 说明 |
|------|-----|------|
| React Router | `useSearchParams()` | 获取 URL 查询参数 |
| React Router | `useParams()` | 获取路由路径参数 |
| React Router | `useLocation()` | 获取当前 location 对象 |
| React Router | `this.props.location.query` | 类组件中的参数 |
| Vue Router | `route.query` | 获取查询参数 |
| Vue Router | `route.params` | 获取路由参数 |
| Vue Router | `this.$route.query` | Options API |
| Vue Router | `useRoute()` | Composition API |

### 3. 跨窗口通信

| API | 说明 | 风险点 |
|-----|------|--------|
| `window.postMessage` | 跨窗口消息发送 | 未校验 origin |
| `message` 事件监听 | 接收跨窗口消息 | 未校验 data 类型和内容 |
| `window.opener` | 父窗口引用 | 可用于获取来源页数据 |

---

## 二、危险输出点（Output Sinks）

### 1. DOM API

```javascript
// 高危：直接赋值
element.innerHTML = userInput;
element.outerHTML = userInput;

// 高危：直接插入
document.write(userInput);
document.writeln(userInput);

// 高危：邻近插入
element.insertAdjacentHTML("beforeend", userInput);
```

### 2. jQuery

```javascript
// 高危
$("#result").html(userInput);
$("#result").append(userInput);
$("#result").prepend(userInput);
$("#result").after(userInput);
$("#result").before(userInput);
```

### 3. React

```jsx
// 高危
<div dangerouslySetInnerHTML={{ __html: userInput }} />

// 中危：href 属性直接使用用户输入
<a href={userInput}>click</a>
```

### 4. Vue

```html
<!-- 高危 -->
<div v-html="userInput"></div>

<!-- 中危：属性绑定 -->
<img :src="userInput">
```

### 5. 代码执行

```javascript
// 高危
eval(userInput);
setTimeout("..." + userInput + "...", 100);
new Function(userInput);
```

### 6. 模板字符串拼接 HTML

```javascript
// 高危
const html = `<div>${userInput}</div>`;
element.innerHTML = html;
```

---

## 三、常见安全误区

| 误区 | 说明 |
|------|------|
| "我用了 React，默认安全" | React 默认转义 {} 中的文本，但 `dangerouslySetInnerHTML` 跳过转义 |
| "我用了 Vue，默认安全" | `v-html` 和 `:href` 等绑定需要额外的安全处理 |
| "postMessage 只接收自己的消息" | 没有校验 origin 和 source 就可以被任意页面伪造 |
| "用了 encodeURI 就够了" | encodeURI 不转义 HTML 特殊字符，需用 encodeURIComponent |
| "jQuery 的 append 会自动编码" | `.append()` 如果接收字符串，直接解析为 HTML |
| "我对整个输入做了 escape" | escape() 已废弃且不完整，需用专用的编码库 |

---

## 四、数据流检测模式

### 模式 1：URL → DOM（最常见）
```
URL parameter → location.search → 解码 → innerHTML / v-html / .html()
```

### 模式 2：URL → React Router → dangerouslySetInnerHTML
```
URL → useSearchParams / useParams → state → dangerouslySetInnerHTML
```

### 模式 3：URL → Vue Router → v-html
```
URL → route.query / route.params → component prop/state → template v-html
```

### 模式 4：postMessage → DOM
```
other window → postMessage → addEventListener("message") → innerHTML
```

### 模式 5：JSONP callback → page
```
URL callback param → JSP/API → script tag → eval → DOM
```
