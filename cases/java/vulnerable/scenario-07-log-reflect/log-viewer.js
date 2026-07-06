/**
 * 日志查看器 JavaScript
 * 
 * 负责日志的 AJAX 加载、实时刷新、详情展示等
 * 存在 DOM-based XSS 漏洞
 */

// ---- 加载日志详情（通过 AJAX） ----

function loadLogDetail(logId) {
    fetch("/admin/logs/" + logId)
    .then(response => response.text())
    .then(html => {
        // 直接解析 HTML 并提取详情内容
        var parser = new DOMParser();
        var doc = parser.parseFromString(html, "text/html");

        // 漏洞点: 从后端返回的 HTML 中提取详情内容
        var detailContent = doc.querySelector(".detail-content");
        if (detailContent) {
            // 直接通过 innerHTML 插入到当前页面
            // detailContent.innerHTML 可能包含用户提交的恶意脚本
            document.getElementById("logDetailPanel").innerHTML = detailContent.innerHTML;
        }
    })
    .catch(error => {
        console.error("加载日志详情失败:", error);
    });
}

// ---- 搜索日志（AJAX 实时搜索） ----

function searchLogs() {
    var keyword = document.getElementById("searchKeyword").value;

    fetch("/admin/logs/search?q=" + encodeURIComponent(keyword))
    .then(response => response.json())
    .then(logs => {
        var tbody = document.querySelector(".log-table tbody");
        tbody.innerHTML = "";  // 清空现有行

        logs.forEach(function(log) {
            // 
            // 漏洞点: 使用模板字符串拼接 HTML
            // log.message 和 log.detail 可能包含用户输入
            // 直接通过 innerHTML 插入 DOM
            //
            var rowHtml = `
                <tr class="log-row log-level-${log.level}">
                    <td>${log.id}</td>
                    <td><span class="level-badge">${log.level}</span></td>
                    <td>${log.action}</td>
                    <td>${log.user}</td>
                    <td class="log-message">${log.message}</td>
                    <td class="log-detail">${log.detail}</td>
                    <td>${log.formattedTime}</td>
                    <td><a href="#" onclick="loadLogDetail(${log.id})">查看</a></td>
                </tr>
            `;
            tbody.innerHTML += rowHtml;  // 未转义的用户内容被插入 DOM
        });
    })
    .catch(error => {
        console.error("搜索日志失败:", error);
    });
}

// ---- 实时刷新日志 ----

function autoRefreshLogs() {
    var currentLevel = document.querySelector("select[name='level']").value;
    var keyword = document.querySelector("input[name='keyword']").value;

    fetch("/admin/logs?level=" + currentLevel + "&keyword=" + keyword + "&ajax=true")
    .then(response => response.text())
    .then(html => {
        // 漏洞点: 直接使用 jQuery 的 .html() 方法替换表格内容
        // 如果返回的 HTML 包含用户输入，将触发 XSS
        $(".log-table tbody").html(
            $(html).find(".log-table tbody").html()
        );
    });
}

// ---- 初始化 ----

document.addEventListener("DOMContentLoaded", function() {
    // 绑定搜索按钮
    document.getElementById("searchBtn").addEventListener("click", searchLogs);

    // 绑定回车键搜索
    document.getElementById("searchKeyword").addEventListener("keypress", function(e) {
        if (e.key === "Enter") {
            searchLogs();
        }
    });
});
