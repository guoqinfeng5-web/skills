<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>用户反馈 - Scenario 03</title>
    <link rel="stylesheet" href="/css/common.css"/>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
</head>
<body>
<div class="container">
    <h1>用户反馈 <span class="tag">Scenario 03</span></h1>
    <div class="feedback-form">
        <div class="form-group">
            <label>用户名</label>
            <input type="text" id="username"/>
        </div>
        <div class="form-group">
            <label>反馈内容</label>
            <textarea id="content" rows="4"></textarea>
        </div>
        <button id="submitBtn">提交反馈</button>
    </div>
    <div id="feedbackList" class="feedback-list"></div>
</div>
<script src="/js/scenario03-feedback.js"></script>
</body>
</html>
