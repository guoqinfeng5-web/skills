<%@ page language="java" contentType="application/javascript; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  // 修复：严格校验 callback 参数，只允许字母数字和下划线
  String callback = request.getParameter("callback");
  if (callback == null || !callback.matches("^[a-zA-Z_][a-zA-Z0-9_.]*$")) {
    // 不安全的 callback 直接返回错误，拒绝执行
    out.println("/** 错误：无效的 callback 参数 */");
    return;
  }

  // 安全的 JSONP 响应
  String jsonData = "["
    + "{\"id\":1,\"name\":\"商品A\",\"price\":99.9},"
    + "{\"id\":2,\"name\":\"商品B\",\"price\":199.9}"
    + "]";

  // callback 已经过校验，可安全拼接
  out.println(callback + "(" + jsonData + ");");
%>
