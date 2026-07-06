# Java 反射型 XSS 检测模式

## 一、输入源（Input Sources）

### 1. Servlet API

| API | 说明 | 风险等级 |
|-----|------|---------|
| `HttpServletRequest.getParameter(String name)` | 获取单个请求参数 | 高 |
| `HttpServletRequest.getParameterValues(String name)` | 获取多值参数 | 高 |
| `HttpServletRequest.getParameterMap()` | 获取所有参数 | 高 |
| `HttpServletRequest.getQueryString()` | 获取原始查询字符串 | 高 |
| `HttpServletRequest.getHeader(String name)` | 获取请求头 | 中 |
| `HttpServletRequest.getCookies()` | 获取 Cookie | 中 |

### 2. Spring MVC 注解

| 注解 | 说明 | 常见误区 |
|------|------|---------|
| `@RequestParam` | 绑定请求参数 | 认为类型转换就是安全过滤 |
| `@PathVariable` | 绑定路径变量 | 路径参数常被忽略直接输出 |
| `@RequestBody` | 绑定请求体 JSON | JSON 反序列化后取字段输出 |
| `@RequestHeader` | 绑定请求头 | User-Agent / Referer 常被忽略 |

---

## 二、危险输出点（Output Sinks）

### 1. JSP 输出

```jsp
<!-- 高危：Scriptlet 直接输出 -->
<%= request.getParameter("name") %>

<!-- 高危：EL 表达式直接输出用户输入 -->
${param.name}

<!-- 高危：out.print / out.write -->
<% out.print(request.getParameter("data")); %>

<!-- 中危：表单 value 回显 -->
<input value="${param.keyword}">

<!-- 中危：JavaScript 上下文拼接 -->
<script>
    var user = "${param.username}";
</script>
```

### 2. Spring MVC 视图

```java
// 高危：model 传递未转义数据
model.addAttribute("result", unescapedInput);

// 高危：返回 HTML 字符串
@ResponseBody
public String handler(@RequestParam String q) {
    return "<div>" + q + "</div>";
}

// 中危：Redirect 参数拼接
return "redirect:/search?q=" + query;
```

### 3. Response 直接输出

```java
response.getWriter().write(unescapedInput);
```

---

## 三、常见安全误区

| 误区 | 说明 |
|------|------|
| "用了 ModelAttribute 会自动过滤" | ModelAttribute 只是数据绑定，不做 XSS 过滤 |
| "Service 层做了业务校验" | 业务校验 ≠ XSS 编码 |
| "这个字段是数字不用转义" | 但数字可能被拼接到 HTML 属性或 JS 中 |
| "自定义标签内部已经处理了" | 自定义标签可能没有做上下文敏感的编码 |

## 四、检测优先级

1. **同文件数据流**: Controller @RequestParam → addAttribute → JSP EL 输出
2. **跨文件数据流**: Controller → Service → JSP 输出
3. **API 模式**: @ResponseBody 返回字符串中包含用户输入
4. **间接模式**: 输入经过 JSON 序列化/反序列化后在页面展示
