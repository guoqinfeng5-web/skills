---
title: "Scenario-01: 多层参数透传的反射型 XSS"
layout: scenario
---

## 业务背景

这是一个电商网站的**商品搜索**功能模块。用户在搜索框中输入关键词，经过 Controller → Service → BusinessWrapper 三层处理后，最终在 JSP 页面上展示搜索结果和"搜索摘要"信息。

该业务团队为了"保护代码结构"，在 Service 和 Controller 之间加入了一个 `BusinessWrapper` 层，用于统一封装业务返回数据。但是，团队在封装过程中**没有对用户输入进行转义**，导致原始输入被透传到 JSP 的 EL 表达式中。

## 数据流路径

```
Browser (搜索关键词) 
  → GET /search?q=<script>...
  → SearchController.search(@RequestParam q)
  → SearchService.search(query) 
  → BusinessWrapper.wrap(result, rawQuery)  ← 未转义
  → ModelAndView.addObject("result", wrapper)
  → search.jsp: ${result.displayContent} ← 触发 XSS
```

## 脆弱点说明

### 主要漏洞点（预期扫描命中）

| 文件 | 行号范围 | 规则 ID | 说明 |
|------|----------|---------|------|
| SearchService.java | ~第15行 | JAVA-IN-002 | @RequestParam 输入源 |
| SearchController.java | ~第40行 | JAVA-OUT-007 | model.addAttribute 传递未转义数据 |
| search.jsp | ~第25行 | JAVA-OUT-002 | EL 表达式 ${result.displayContent} 直接输出 |
| search.jsp | ~第30行 | JAVA-OUT-002 | EL 表达式 ${result.searchQuery} 直接输出 |
| SearchService.java | ~第80行 | — | getDisplayContent() 方法未转义返回原始输入 |

### 为什么这是漏洞

1. `SearchController` 获取用户输入后，传给 `SearchService`
2. `SearchService.search()` 调用 `BusinessWrapper.wrap()` 封装结果
3. `BusinessWrapper` 将 `displayContent` 设置为包含原始搜索关键词的 HTML 片段（没有转义）
4. JSP 中使用 `${result.displayContent}` 直接输出，如果关键词包含 `<script>alert(1)</script>`，将执行恶意脚本
5. 虽然 `SearchService` 做了一些业务处理（分词、统计等），但这些处理**没有对输出到 HTML 的内容做转义**

### 修复建议

- 在 JSP 中使用 `<c:out value="${result.displayContent}" />` 而非 `${result.displayContent}`
- 或在 `BusinessWrapper` / `SearchService` 中使用 `HtmlUtils.htmlEscape()` 对用户输入进行编码
- 最好的方式是**在输出层统一转义**，而不是在业务层
