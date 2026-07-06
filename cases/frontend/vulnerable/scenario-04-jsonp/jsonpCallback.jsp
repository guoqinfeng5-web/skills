<%@ page language="java" contentType="application/javascript; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  // 二级 JSONP 接口，用于获取单个用户的详细信息
  String callback = request.getParameter("callback");
  String userId = request.getParameter("uid");

  if (callback == null || callback.isEmpty()) {
    callback = "jsonpCallback";
  }

  // 模拟根据 uid 查询用户详情
  String userJson = "{\"id\":" + userId + ",\"profile\":\"<b>用户简介</b>\"}";
%><%= callback %>(<%= userJson %>);
