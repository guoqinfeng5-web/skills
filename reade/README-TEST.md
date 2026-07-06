# 反射型 XSS 测试项目

> **警告**：本项目故意包含多种反射型 XSS 漏洞，仅用于 XSS Skill 静态扫描验证与修复建议测试，禁止部署到生产环境。

## 项目概述

基于 **Spring Boot 2.7 + JSP** 的传统 Web 项目，同时包含 **7 个前端静态场景**，覆盖 XSS Skill 定义的全部 14 种反射型漏洞模式。

除 14 个独立场景外，项目还包含 **跨模块联动层**（`common/`、`portal/`），模拟真实业务中「用户输入经多模块流转、最终在不同页面二次展示」的数据流，用于检验 Skill 的**跨文件追踪**能力。

| 类型 | 数量 | 技术栈 |
|------|------|--------|
| Java 后端 | 7 + 联动层 | Spring MVC、JSP EL、Scriptlet、自定义 Tag、@ResponseBody |
| 前端 | 7 + 联动页 | 原生 JS、Vue 3、jQuery、JSONP、postMessage |
| 共享模块 | 2 | `AuditTrailService`（审计桥接）、`PortalService`（门户聚合） |

## 快速启动

```bash
cd Test
mvn spring-boot:run
```

访问 http://localhost:8080/ 查看全部场景入口。

---

## 推荐入口：跨模块联动

优先使用以下入口进行 Skill 扫描验证——比单场景更接近真实项目，需要跨包、跨文件追踪数据流。

| 入口 | 路径 | 说明 |
|------|------|------|
| JSP 统一门户 | `/portal/dashboard` | 聚合 session、搜索、用户资料、审计日志 |
| 前后端分离门户 | `/frontend/portal/index.html?q=test&user=admin` | 单页串联 Portal API + Feedback API + Log API |

### 联动数据流

```
用户输入 q
  → SearchController / PortalController
  → SearchService → BusinessWrapper（HTML 拼接，未转义）
  → dashboard.jsp EL（${searchResult.displayContent}、${welcomeHtml}）
  ↘ AuditTrailService → LogService.recordAction()
  → scenario07 admin.jsp EL / AJAX innerHTML（二次反射）

登录 username（POST /scenario02/auth/login）
  → AuthController 拼接 errorMsg
  → error.jsp <%= %>（直接反射）
  ↘ AuditTrailService → LogService
  → /portal/dashboard 最近日志 / scenario07 管理页

反馈 content（POST /scenario03/api/feedback/submit）
  → FeedbackService → FeedbackController 返回 HTML
  → feedback.js innerHTML
  ↘ AuditTrailService → LogService → 日志二次展示

文件上传 filename / description
  → FileUploadController → uploadResult.jsp EL
  ↘ AuditTrailService → LogService
```

### 共享模块说明

| 模块 | 路径 | 作用 |
|------|------|------|
| `AuditTrailService` | `com.xsslab.common` | 搜索/登录/反馈/上传共用，将用户输入写入 `LogService` |
| `UserSessionHelper` | `com.xsslab.common` | 管理 session 中的 `currentUser`、`lastSearchQuery` |
| `PortalService` | `com.xsslab.portal` | 聚合 SearchService、ProfileService、LogService，构建门户视图 |
| `PortalController` | `com.xsslab.portal` | `/portal/dashboard` 页面 + `/portal/api/search-snippet` AJAX 接口 |

登录成功后跳转 `/portal/dashboard`（`scenario02` 与门户联动）。

---

## 场景清单

### Java 后端（独立场景）

| ID | 场景 | 路径 | 输入源 | 输出点 | 预期 Skill 命中 |
|----|------|------|--------|--------|----------------|
| 01 | 多层参数透传 | `GET /scenario01/search?q=` | `@RequestParam q` | JSP `${result.displayContent}` | JAVA-IN-002, JAVA-OUT-002, JAVA-OUT-007 |
| 02 | 错误信息回显 | `POST /scenario02/auth/login` | `username` | JSP `<%= errorMsg %>` | JAVA-OUT-001 (scriptlet) |
| 03 | JSON→HTML API | `/scenario03/feedback` | `@RequestBody` | `@ResponseBody` HTML + `innerHTML` | JAVA-OUT-005, FE-OUT-001 |
| 04 | 自定义 JSP 标签 | `/scenario04/profile?username=` | `@RequestParam` | `UserProfileTag.out.print()` | JAVA-OUT-004 |
| 05 | 文件上传回显 | `/scenario05/file/upload` | `getOriginalFilename()` | JSP `${filename}` | JAVA-IN-006, JAVA-OUT-002 |
| 06 | 重定向拼接 | `/scenario06/auth/login?redirect=` | `redirect` 参数 | `redirect:` + `sendRedirect()` | JAVA-OUT-008 |
| 07 | 日志二次反射 | `/scenario07/admin/logs` | 用户输入→存储→展示 | JSP EL + AJAX `innerHTML` | JAVA-OUT-002, FE-OUT-001 |

> **联动说明**：scenario01 搜索时经 `AuditTrailService` 写入 scenario07 日志；scenario02 登录失败/成功同样写入审计日志。

### 联动场景（跨模块）

| ID | 场景 | 路径 | 涉及模块 | 关键数据流 |
|----|------|------|----------|-----------|
| P01 | JSP 统一门户 | `/portal/dashboard?q=` | 01 + 04 + 07 + common | session → SearchService → EL + Tag + 日志表 |
| P02 | Portal AJAX 搜索 | `GET /portal/api/search-snippet?q=` | 01 + portal | query → SearchService → HTML 片段 → jQuery `.html()` |
| P03 | 前后端分离门户 | `/frontend/portal/index.html` | 01 + 03 + 06 + 07 | 多 API 调用 + innerHTML / jQuery `.html()` |

### 前端（独立场景）

| ID | 场景 | 路径 | 输入源 | 输出点 | 联动说明 |
|----|------|------|--------|--------|----------|
| F01 | 搜索词高亮 | `/frontend/scenario01-search/?q=` | `location.search` | `innerHTML` | **已联动后端**：调用 `/portal/api/search-snippet` |
| F02 | Hash 路由 | `/frontend/scenario02-hash/#/content?name=` | hash query | Vue `v-html` | — |
| F03 | postMessage | `/frontend/scenario03-postmessage/parent.html?user=` | URL→postMessage | `innerHTML` | — |
| F04 | JSONP callback | `/frontend/scenario04-jsonp/?cb=` | `callback` 参数 | 服务端 JS 拼接 | 后端 `/scenario04/api/jsonp` |
| F05 | 模板字符串 | `/frontend/scenario05-template/?inject=` | URL/API 数据 | 模板→`innerHTML` | — |
| F06 | jQuery 遗留 | `/frontend/scenario06-jquery/?user=` | URL params | jQuery `.html()` | 联动门户页复用相同模式 |
| F07 | URL 表单预填 | `/frontend/scenario07-form/?content=` | base64 URL 参数 | Vue `v-html` | — |

---

## 测试 Payload 示例

```
# 联动门户 - 搜索透传 + 日志二次反射
/portal/dashboard?q=<script>alert(1)</script>

# 联动门户 - AJAX 搜索片段
/portal/api/search-snippet?q=<img src=x onerror=alert(1)>

# 前后端分离门户（搜索 + 反馈 + 日志）
/frontend/portal/index.html?q=<script>alert(1)</script>&user=<b>admin</b>

# Scenario 01 - EL 反射
/scenario01/search?q=<script>alert(1)</script>

# Scenario 02 - Scriptlet 反射（POST username）
<script>alert(document.cookie)</script>

# Scenario 04 - 自定义标签
/scenario04/profile?username=<img src=x onerror=alert(1)>&level=<b>test</b>

# Scenario 06 - 属性注入
/scenario06/auth/login?redirect="><script>alert(1)</script>

# Frontend F01（调用后端 API）
/frontend/scenario01-search/index.html?q=<img src=x onerror=alert(1)>

# Frontend F04 - JSONP
/frontend/scenario04-jsonp/legacy-page.html?cb=alert(1)//
```

---

## 目录结构

```
Test/
├── pom.xml
├── src/main/java/com/xsslab/
│   ├── XssTestApplication.java
│   ├── HomeController.java
│   ├── common/                          # 跨模块共享
│   │   ├── AuditTrailService.java       # 审计桥接 → LogService
│   │   └── UserSessionHelper.java       # Session 工具
│   ├── portal/                          # 统一门户
│   │   ├── PortalController.java
│   │   └── PortalService.java
│   ├── scenario01/ … scenario07/        # 7 个独立后端场景
│   └── ...
├── src/main/webapp/WEB-INF/
│   ├── jsp/
│   │   ├── index.jsp                    # 场景索引（含联动入口）
│   │   ├── portal/dashboard.jsp         # JSP 统一门户
│   │   └── scenario01-07/               # 各场景 JSP
│   └── tags/userProfile.tld             # 自定义标签 TLD
└── src/main/resources/static/
    ├── css/common.css
    ├── js/
    │   ├── scenario03-feedback.js
    │   ├── scenario07-log-viewer.js
    │   └── portal-dashboard.js          # 门户 AJAX 搜索
    └── frontend/
        ├── portal/                      # 前后端分离联动页
        │   ├── index.html
        │   └── portal-app.js
        └── scenario01-07/               # 7 个独立前端场景
```

---

## 供 XSS Skill 扫描的使用方式

### 扫描策略建议

| 优先级 | 扫描目标 | 考察能力 |
|--------|----------|----------|
| 1 | `/portal/dashboard`、`/frontend/portal/` | 跨模块、跨文件数据流追踪 |
| 2 | `AuditTrailService` 调用链 | 二次反射（存储→展示）识别 |
| 3 | 14 个独立场景 | 单模式输入源/输出点匹配 |

### 预期 Skill 行为

1. 将 `Test/` 项目路径提供给 XSS Skill，触发「XSS 扫描」或「安全审计」
2. Skill 应识别 **14 个独立场景 + 3 个联动场景** 中的输入源 → 数据流 → 危险输出点
3. 对联动场景，Skill 需追踪跨包路径，例如：
   - `SearchService.search()` → `AuditTrailService.trace()` → `LogService.recordAction()` → `admin.jsp ${log.message}`
   - `PortalService.buildSearchSnippet()` → `portal-dashboard.js` → `$("#ajaxSearchResult").html(html)`
4. 预期输出：按严重程度排序的风险报告 + OWASP 修复建议
5. 可与 `../cases/` 目录中的 safe 版本对照验证修复建议质量

### Skill 扫描难点（故意设计）

- **跨层透传**：`BusinessWrapper` 业务包装 ≠ 转义，数据继续流向 JSP
- **二次反射**：输入先写入 `LogService`，后在 admin/portal 页面展示
- **前后端混合**：后端返回 HTML 片段，前端 `innerHTML` / jQuery `.html()` 渲染
- **Session 携带**：登录 username 经 session 进入门户欢迎语，未经转义输出

---

## 技术说明

- **Java 11** + **Spring Boot 2.7.18** + **JSP/JSTL**
- 打包方式：`war`（支持 JSP 编译）
- 前端场景使用 CDN 加载 Vue 3 / jQuery，无需 Node.js 构建
- 默认端口：`8080`
- 测试登录凭据：`admin` / `admin123`（登录后跳转 `/portal/dashboard`）
