# Java 存储型 XSS 检测模式

## 一、典型数据流模式

### 模式 1：评论/留言板（最简单常见）

```
用户输入（content）
  → @RequestParam / @RequestBody
    → Service 层
      → repository.save(comment)          ← 存储入口
        → DB (comments 表)
      → repository.findAll() / findByPage()  ← 检索入口
    → Controller
      → model.addAttribute("comments", ...)
        → JSP ${comment.content}           ← 未转义输出
```

注意：同一 Controller 中可能有 save 和 list 两个端点，需要跨请求追踪。

### 模式 2：用户资料编辑

```
POST /profile/edit
  username=attacker_input
  bio=<script>alert(1)</script>
    → ProfileController.update()
      → repository.save(profile)           ← 存储入 DB
        → Profile 表的 bio 列

GET /profile/view
  → repository.findByUsername()
    → profile.getBio()
      → model.addAttribute("bio", ...)
        → JSP ${bio}                       ← 未转义输出
```

陷阱："用户自己编辑的资料，只给用户自己看"——但可能被其他用户访问（如社交页面），或管理员后台查看。

### 模式 3：审计日志存储展示（跨模块）

```
ControllerA.report()
  → auditService.log(action, user, detail)  ← 存储日志
    → LogRepository.save(logEntry)
      → logs 表

ControllerB.viewLogs()
  → logRepository.findAll()
    → ${log.message} / ${log.detail}        ← 日志二次展示
```

典型场景：搜索日志、操作审计、错误日志管理后台。输入可能来自多个模块聚合。

### 模式 4：文件上传 → 内容读取 → 展示

```
POST /file/upload
  → file.transferTo(savedFile)             ← 存储到文件系统

GET /file/preview/{id}
  → Files.readString(savedPath)            ← 读取文件内容
    → model.addAttribute("preview", content)
      → JSP ${preview}                     ← 未转义输出
```

特别危险：上传 SVG / HTML 文件且直接输出时，可能导致 XSS + 文件上传组合漏洞。

### 模式 5：Redis 缓存 → 反序列化 → 展示

```
@CachePut(value = "users", key = "#username")
public UserProfile loadProfile(String username) {
    return buildProfile(username);          ← 缓存存储
}

@Cacheable(value = "users", key = "#username")
public UserProfile loadProfile(String username) {
    return repository.findByUsername(username);
}
  → controller
    → JSP ${profile.bio}                   ← 缓存读取后输出
```

### 模式 6：批量导出（CSV/Excel）

```
List<User> users = userRepository.findAll();
StringBuilder csv = new StringBuilder();
for (User u : users) {
    csv.append(u.getDisplayName()).append(",")  ← 存储数据拼入导出文件
    csv.append(u.getBio()).append("\n");
}
response.setContentType("text/csv");
response.getWriter().write(csv.toString());     ← 输出到导出文件
```

注意：CSV 注入可导致 Excel 公式执行，属于存储型 XSS 的变种。

---

## 二、JPA 实体数据流追踪方法

```java
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    private Long id;

    @Column(name = "content")
    private String content;          // ← 追踪点：谁在 setContent() 赋值？

    @Column(name = "author")
    private String author;           // ← 追踪点：谁在 setAuthor() 赋值？

    // getter / setter
    public String getContent() { return content; }   // ← 追踪点：谁在 getContent() 获取？
    public String getAuthor() { return author; }     // ← 追踪点：谁在 getAuthor() 获取？
}
```

追踪策略：
1. 找到实体类 → 标记所有 String 类型的字段
2. 搜索字段的 setter 调用位置（存储入口）
3. 搜索字段的 getter 调用位置（读取入口，可能跨文件）
4. 对每个读取入口，追踪数据到输出点

---

## 三、存储型 XSS 特有陷阱

| 陷阱 | 说明 |
|------|------|
| "数据在数据库里就是安全的" | 数据库存储原始数据，不执行也不转义 HTML |
| "存储前校验过了" | 输入校验（如长度/格式）≠ 输出编码 |
| "这是管理后台才看" | 管理员浏览器同样执行 JS。攻破管理员可导致提权 |
| "用了 JPA 自动映射" | JPA 只是 ORM 映射，不会自动编码 |
| "用了 MyBatis #{} 占位符" | #{} 防 SQL 注入，不防 XSS |
| "Redis 缓存只是内存" | 缓存数据反序列化后同原始数据，需要同等对待 |
| "文件存的是纯文本" | 读出来后拼到 HTML 就是 HTML |
| "存的时候转义了" | 存储时转义会破坏原始数据。应在输出时按上下文编码 |
