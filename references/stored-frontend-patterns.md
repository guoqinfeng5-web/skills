# 前端存储型 XSS 检测模式

## 一、典型数据流模式

### 模式 1：localStorage → innerHTML

```
URL params / user input
  → localStorage.setItem("userPrefs", rawData)    ← 存储

  → localStorage.getItem("userPrefs")              ← 读取
    → element.innerHTML = rawData                  ← 输出
```

常见场景：用户偏好设置保存（主题、布局、自定义内容），下次访问时恢复。

### 模式 2：Cookie → DOM 操作

```
Server set-cookie: displayName=<script>...
  → document.cookie 自动存储                     ← 存储

  → document.cookie 解析
    → document.getElementById("greeting").innerHTML = displayName  ← 输出
```

常见场景：Cookie 中保存的用户信息在页面中展示。

### 模式 3：IndexedDB → 列表渲染

```
fetch("/api/data")
  → objectStore.add(responseData)                 ← 存储到 IndexedDB

  → objectStore.getAll()
    → data.forEach(item => {
        container.innerHTML += "<div>" + item.content + "</div>"  ← 输出
      })
```

常见场景：PWA 离线应用、客户端缓存加速。

### 模式 4：postMessage → localStorage → 同源页面读取

```
window.addEventListener("message", function(e) {
    localStorage.setItem("shared", e.data.content);  // ← 未校验 origin
});

// 另一页面
const data = localStorage.getItem("shared");
document.getElementById("output").innerHTML = data;  // ← 输出
```

跨窗口通信写入 localStorage，同源的其他页面读取后展示。

### 模式 5：表单自动保存 → 恢复

```
$("#editor").on("input", function() {
    localStorage.setItem("draft", $(this).val());   // ← 自动存储草稿
});

$(function() {
    var draft = localStorage.getItem("draft");       // ← 恢复草稿
    $("#preview").html(draft);                       // ← 直接渲染
});
```

常见场景：富文本编辑器自动保存草稿。

---

## 二、检测优先级

1. **localStorage/sessionStorage → innerHTML/v-html/.html()**
   - 同一域内，读取后直接赋值到 DOM

2. **Cookie → DOM 操作**
   - Cookie 不可信，可被子域或 XSS 设置

3. **IndexedDB → 列表/详情渲染**
   - 离线应用常见模式

4. **Cache API → 页面渲染**
   - Service Worker 缓存的数据反哺页面

5. **postMessage → 浏览器存储 → 读取**
   - 跨窗口 + 存储的组合攻击面

---

## 三、安全误区和陷阱

| 误区 | 说明 |
|------|------|
| "localStorage 是前端自己的数据" | 可被同源任意 JS 写入，包括第三方脚本 |
| "Cookie 设置了 HttpOnly" | HttpOnly 不能防止 Cookie 在前端被读取并展示到 DOM |
| "IndexedDB 不会暴露" | 同源任何页面都可读取 IndexedDB 数据 |
| "缓存的数据是应用自己写的" | 缓存写入路径如果包含用户输入，读取时需同等对待 |
| "sessionStorage 关闭就没了" | 只要页面开着就可能被利用 |

---

## 四、常见场景识别

看前端代码时，快速定位存储型 XSS 的模式关键词：

```
搜索: localStorage.setItem / localStorage.getItem
     sessionStorage.setItem / sessionStorage.getItem
     document.cookie
     indexedDB.open / objectStore
     caches.open / cache.put / cache.match
     配合 innerHTML / .html() / v-html / dangerouslySetInnerHTML
```
