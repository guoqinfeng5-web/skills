---
name: xss
description: "对 Java / 前端项目进行跨站脚本(XSS)漏洞全面静态分析，覆盖反射型、存储型、DOM 型及其他类型。适用场景：Spring + JSP 整体项目、Spring Boot REST + React/Vue/原生JS 前后端分离项目。通过数据分析流识别用户输入未转义即输出到 HTML/JS/CSS/URL 上下文的风险，定位问题代码并给出基于 OWASP 的修复建议。触发词：XSS扫描、安全审计、代码安全检查、Reflected XSS、Stored XSS、DOM XSS、跨站脚本。"
---

# XSS 全面静态分析

## 必读检查清单

开始分析前，逐项确认已完成以下步骤：

- [ ] 读取 `rules/java-input-sources.yaml` 并对照源码排查所有输入源
- [ ] 读取 `rules/frontend-input-sources.yaml`（如涉及前端代码）
- [ ] 读取 `rules/java-output-sinks.yaml` 检查每个输出点的上下文编码
- [ ] 读取 `rules/frontend-output-sinks.yaml`（如涉及前端代码）
- [ ] 读取 `rules/java-safe-functions.yaml`，确认数据流中是否存在安全函数阻断
- [ ] 读取 `rules/frontend-safe-functions.yaml`（如涉及前端代码）
- [ ] 如涉及存储型路径，读取 `rules/java-storage-sinks.yaml` 和 `java-retrieval-sources.yaml`
- [ ] 如涉及前端存储型路径，读取 `rules/frontend-storage-sinks.yaml` 和 `frontend-retrieval-sources.yaml`
- [ ] 按下方模板格式输出每个漏洞（不可改动字段名称和顺序）

## 输出格式模板（必须严格遵循）

**你必须严格遵循以下模板输出每个漏洞，不得改动字段名称和顺序。以下模板是唯一接受的输出格式。**

```
[高危] [反射型] XSS — SearchController.java:18
  数据流：@RequestParam("q") @ SearchController.java:18 → SearchService.search() → BusinessWrapper.wrap() 拼接 HTML → search.jsp:20 result.displayContent
  上下文类型：HTML
  代码片段(必须展示)：
  // SearchController.java:18
  public ModelAndView search(@RequestParam("q") String query, ...) {
      SearchResult result = searchService.search(query, page);
      mav.addObject("result", result);
      ...
  }
  // BusinessWrapper.java:14
  sb.append("当前搜索词：").append(rawQuery);  // rawQuery 未转义直接拼接
  // search.jsp:20
  <div class="search-summary">${result.displayContent}</div>
  问题说明：@RequestParam 接收的用户输入经过 Service 层传到 BusinessWrapper，由 StringBuilder 拼接为 HTML 片段，最终在 JSP 中通过 EL 表达式输出。整条路径上没有经过任何 HtmlUtils.htmlEscape() 或 <c:out> 转义。
  修复建议：
  1. 修复策略：在数据进入 BusinessWrapper 之前进行 HTML 编码（HTML Body 上下文，OWASP RULE #1）
  2. 代码级修复：
     - Before (易受攻击):
       SearchController.java: SearchResult result = searchService.search(query, page);
     - After (安全修复):
       SearchController.java: String safeQuery = HtmlUtils.htmlEscape(query); SearchResult result = searchService.search(safeQuery, page);
```

---
## 思维方式

> **用户的输入从哪里进来？经过什么处理（存储/传输/变换）？最终输出到哪里？输出时是否进行了上下文正确的编码？**

回答这四个问题，沿数据流逐个检查断点，任何一处阻断则漏洞不存在。

### 数据流三段式

```
[输入源] ──→ [中间处理/存储] ──→ [输出点]
                │
                ├── 有编码/转义/消毒吗？
                └── 编码是否匹配输出上下文？
```

---

## 第一步：判断 XSS 类型

扫描代码前，根据数据流特征判断属于以下哪一类（可能同时存在多类）：

### 反射型（Reflected XSS）
**特征**：用户输入 → 服务器端即时处理 → 同一 HTTP 响应中回显
**检测标志**：
- `@RequestParam` / `request.getParameter()` 在 Controller 中直接传给 View
- JSP 中 `${param.xxx}` 或 `<%= request.getParameter() %>`
- `@ResponseBody` 返回包含用户输入的 HTML 字符串
- URL 参数 → 前端 DOM

### 存储型（Stored XSS）
**特征**：用户输入 → 持久化存储 → 后续跨请求读取 → 回显
**检测标志**：
- 输入到达 `repository.save()` / `entityManager.persist()` / JDBC insert
- 数据通过 `findAll()` / `select` 读取后在 View 层展示
- 审计日志/操作记录存储后二次展示
- localStorage / IndexedDB 写后读 → DOM

### DOM 型（DOM-based XSS）
**特征**：全部在浏览器端完成，攻击 payload 不经过服务器
**检测标志**：
- source = URL 参数/hash/postMessage/history.state 等浏览器 API
- sink = innerHTML/eval/document.write/v-html 等 DOM API
- 数据流从浏览器 source 到 DOM sink，不需要服务端参与
- fetch/WebSocket 响应被直接用 innerHTML 渲染（response-based DOM XSS）

### 其他类型（由 LLM 判断命名，并给出解释这种类型是怎么回事）
**特征**：不符合上述三类，但存在用户输入到不安全输出的路径
**可能类型**：
- **Mutation XSS**（mXSS）：利用 HTML 解析器差异绕过消毒
- **Universal XSS**（UXSS）：浏览器漏洞或扩展导致的跨源 XSS
- **MIME 类型混淆**：服务器返回错误的 Content-Type 导致脚本执行
- **CSP 绕过**：利用 CSP 策略中的 `unsafe-inline` / `script-src *` 等配置缺陷
- **JSONP 注入**：callback 参数未校验导致任意代码执行
- **SVG/XML 上传 XSS**：上传的 SVG 文件包含脚本，直接访问时执行

---

## 第二步：定位输入源

四种类型的输入源有重叠也有差异。检查代码中以下模式：

**Servlet API：**
`request.getParameter()` / `request.getParameterValues()` / `request.getParameterMap()` / `request.getQueryString()` / `request.getHeader()` / `request.getCookies()` / `request.getPart()`

**Spring 注解：**
`@RequestParam` / `@PathVariable` / `@RequestBody` / `@RequestHeader` / `@CookieValue` / `@ModelAttribute`

**前端 browser source（DOM 型特有）：**
`location.search` / `location.hash` / `location.href` / `location.pathname` / `document.URL` / `document.referrer` / `window.name` / `postMessage` / `URLSearchParams` / `history.state` / `fetch()` 响应 / `XMLHttpRequest` 响应 / `WebSocket.message` / `ServiceWorker.message`

**前端框架路由（DOM 型）：**
React: `useSearchParams()` / `useParams()` / `useLocation()`
Vue: `$route.query` / `route.params` / `useRoute()`

详细清单见 `rules/java-input-sources.yaml`、`rules/frontend-input-sources.yaml`。

---

## 第三步：按类型追踪数据流

### 反射型追踪
```
Controller @RequestParam
  → Service 处理                               ← 检查是否有转义（HtmlUtils/ESAPI）
    → model.addAttribute / ModelAndView
      → JSP ${} / <%= %> / @ResponseBody       ← 检查是否有 <c:out> / fn:escapeXml
```

**常见陷阱（反射型）：**
- "中间层做了业务处理" → 不等于转义
- "用了 StringBuilder 拼接" → 拼接本身不转义
- "@ResponseBody 返回 JSON" → 但 Content-Type 被设为 text/html

### 存储型追踪
```
Controller @RequestParam
  → Service
    → repository.save(entity)                  ← 存储入口，标记数据来源
  → repository.findAll() / findById()          ← 检索入口
    → Controller → model.addAttribute
      → JSP ${} / <%= %> / @ResponseBody       ← 检查是否转义
```

**常见陷阱（存储型）：**
- "输入存的是数据，不涉及展示" → 数据最终会被检索展示
- "在存储层写了 filter/DAO 层处理" → DAO 不涉及编码
- "只在输入时做了校验" → 白名单校验不等于 HTML 编码
- "前端用了框架绑定" → Vue 双花括号 / React {} 确实逃逸，但 v-html / dangerouslySetInnerHTML 不逃逸

### DOM 型追踪
```
browser source (location.search / hash / postMessage)
  → 前端 JS 处理                                ← 检查是否使用安全 API
    → DOM sink (innerHTML / document.write)     ← 检查是否使用 textContent / safe sink
```

**常见陷阱（DOM 型）：**
- "用了 URLSearchParams 或 JSON.parse" → 数据提取方式不影响 XSS，由 sink 决定
- "只在服务端做了校验" → DOM XSS 不经过服务器
- "用了 React/Vue" → 框架的 JSX / template 会逃逸，但 v-html / dangerouslySetInnerHTML 不逃逸

---

### 严重程度分级

| 级别 | 标准 |
|------|------|
| **高危** | 用户输入直接到输出，中间无任何编码/转义/消毒 |
| **中危** | 用户输入经过输出点，但路径上有部分保护或编码不完整 |
| **人工确认-可疑** | 发现输入源或存储源，未发现对应的危险输出（可能跨文件需人工确认） |

### 修复优先级

1. **输出编码**：在输出点使用上下文正确的编码（HTML 上下文用 HtmlUtils.htmlEscape / `<c:out>` / `textContent`）
2. **输入消毒**：使用 Jsoup.clean() / DOMPurify.sanitize() 白名单过滤
3. **CSP 限制**：设置 Content-Security-Policy 头限制脚本来源
4. **架构整改**：返回 JSON 替代 HTML、使用 textContent 替代 innerHTML

---

## 学习资源 — 必须执行的规则检查步骤

### 步骤一：读取输入源规则（对照代码逐一排查）

- [ ] 读取 `rules/java-input-sources.yaml`，列出代码中 **所有出现的输入源**，逐项确认是否已在追踪路径中被覆盖
- [ ] 如果分析包含前端代码，读取 `rules/frontend-input-sources.yaml`，检查 URL 参数/hash/postMessage/路由等 source
- [ ] 检查同一源码处是否有多个输入源同时触发（如 @RequestParam + @PathVariable）

### 步骤二：读取输出点规则（检查每个输出点的上下文编码）

- [ ] 读取 `rules/java-output-sinks.yaml`，对照每个输出点确认上下文编码是否正确
- [ ] 如果分析包含前端代码，读取 `rules/frontend-output-sinks.yaml`，对照 innerHTML/dangerouslySetInnerHTML/v-html 等 sink
- [ ] 重点检查：输出的 Content-Type 是否为 text/html（如果是，必须进行 HTML 编码）

### 步骤三：读取安全函数规则（确认数据流是否已被阻断）

- [ ] 读取 `rules/java-safe-functions.yaml`，检查数据流中是否使用了 HtmlUtils.htmlEscape() / <c:out> / ESAPI 等安全函数
- [ ] 读取 `rules/frontend-safe-functions.yaml`，检查前端是否使用了 textContent / DOMPurify.sanitize() 等安全方式

### 步骤四：如果存在存储型路径，读取存储/检索规则

- [ ] 读取 `rules/java-storage-sinks.yaml`，确认所有持久化存储入口（JPA/JDBC/Redis/文件等）
- [ ] 读取 `rules/java-retrieval-sources.yaml`，确认所有数据检索出口，追踪到输出点
- [ ] 如果涉及前端存储，读取 `rules/frontend-storage-sinks.yaml` 和 `rules/frontend-retrieval-sources.yaml`

### 步骤五：查阅参考资料确认模式

分析对应类型的 XSS 时，必须阅读以下参考资料：

- `references/reflected-java-patterns.md` — 反射型 Java 模式对照
- `references/reflected-frontend-patterns.md` — 反射型/前端 DOM 模式对照
- `references/stored-java-patterns.md` — 存储型 Java 模式对照
- `references/stored-frontend-patterns.md` — 存储型前端模式对照
- `references/dom-based-patterns.md` — DOM 型 XSS 模式对照
- `references/Cross_Site_Scripting_Prevention_Cheat_Sheet.md` — OWASP 官方防护指南
- `references/DOM_based_XSS_Prevention_Cheat_Sheet.md` — DOM 型 XSS 防护
- `references/XSS_Filter_Evasion_Cheat_Sheet.md` — XSS 绕过技术（辅助确认误报/漏报）

### 步骤六：参考示例场景确认判定准确性

读取 `cases/vulnerable/` 和 `cases/safe/` 中各场景的 README 和源码，确保分析覆盖所有常见 XSS 模式。

---

## 人工复核清单

以下情况扫描难以覆盖：
- [ ] 输入数据经过复杂的字符串变换/编解码后再输出
- [ ] 通过反射/动态调用传递的数据流
- [ ] 第三方库/组件内部的 XSS 漏洞
- [ ] 响应头（Content-Type）被设置为 text/html
- [ ] CSP 配置是否存在漏洞（如 unsafe-inline、script-src *）
- [ ] JSONP 接口的 callback 参数校验
- [ ] 文件上传的 SVG/HTML 文件直接访问
- [ ] 富文本编辑器过滤不完整（允许 style/svg 标签）
- [ ] 数据在不同系统间同步（ETL）后的二次展示
