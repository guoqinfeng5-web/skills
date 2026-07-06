<%@ page language="java" contentType="application/javascript; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*, javax.naming.*" %>
<%
  // 从请求中获取 callback 参数 — 无任何校验
  String callback = request.getParameter("callback");
  if (callback == null || callback.isEmpty()) {
    callback = "callback";
  }

  // 模拟从数据库获取用户数据
  String jsonData = "[{\"id\":1,\"name\":\"张三\",\"email\":\"zhangsan@example.com\"},{\"id\":2,\"name\":\"李四\",\"email\":\"lisi@example.com\"}]";

  // 直接拼接 callback — 反射型 XSS 漏洞点
%><%= callback %>(<%= jsonData %>);
