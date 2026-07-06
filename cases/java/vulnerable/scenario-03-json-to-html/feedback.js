/**
 * 反馈页面 JavaScript
 * 
 * 负责提交反馈、加载反馈列表、展示反馈内容
 * 存在 DOM-based XSS 漏洞 —— 直接使用 innerHTML 渲染后端返回的 HTML 片段
 */

// ---- 提交反馈（漏洞触发路径）----

function submitFeedback() {
    const username = document.getElementById("username").value;
    const content = document.getElementById("content").value;
    const contactInfo = document.getElementById("contactInfo").value;

    const requestBody = {
        username: username,
        content: content,
        contactInfo: contactInfo
    };

    fetch("/api/feedback/submit", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(requestBody)
    })
    .then(response => response.text())
    .then(html => {
        // 漏洞点: 直接将后端返回的 HTML 通过 innerHTML 插入 DOM
        // 如果后端未转义用户输入，此处将触发 XSS
        document.getElementById("feedbackList").innerHTML += html;
    })
    .catch(error => {
        console.error("提交反馈失败:", error);
    });
}

// ---- 加载反馈列表 ----

function loadFeedbackList(page) {
    fetch("/api/feedback/list?page=" + (page || 1))
    .then(response => response.text())
    .then(html => {
        // 漏洞点: 使用 jQuery 的 .html() 方法直接插入未验证的 HTML
        $("#feedbackList").html(html);
    })
    .catch(error => {
        console.error("加载评论列表失败:", error);
    });
}

// ---- 提交回复 ----

function submitReply(feedbackId) {
    const replyContent = document.getElementById("replyContent_" + feedbackId).value;

    fetch("/api/feedback/reply", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: "feedbackId=" + feedbackId + "&replyContent=" + replyContent
    })
    .then(response => response.text())
    .then(html => {
        // 漏洞点: 直接插入返回的 HTML
        document.querySelector(".reply-container-" + feedbackId).innerHTML += html;
    });
}

// ---- 绑定事件（页面加载时） ----

document.addEventListener("DOMContentLoaded", function() {
    // 绑定提交按钮
    document.getElementById("submitBtn").addEventListener("click", submitFeedback);

    // 加载第一页反馈
    loadFeedbackList(1);
});
