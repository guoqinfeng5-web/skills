(function ($) {
    "use strict";

    var urlParams = {};
    location.search.substring(1).split("&").forEach(function (pair) {
        var kv = pair.split("=");
        if (kv[0]) urlParams[decodeURIComponent(kv[0])] = decodeURIComponent(kv[1] || "");
    });

    if (urlParams.q) {
        $("#searchInput").val(urlParams.q);
    }

    var userName = urlParams.user || "访客";
    $("#userGreeting").html("欢迎您，<strong>" + userName + "</strong>");

    $("#searchBtn").on("click", function () {
        var q = $("#searchInput").val();
        fetch("/portal/api/search-snippet?q=" + encodeURIComponent(q), { credentials: "include" })
            .then(function (r) { return r.text(); })
            .then(function (html) {
                $("#searchPanel").html(html);
            });
    });

    $("#feedbackBtn").on("click", function () {
        var content = $("#feedbackContent").val();
        fetch("/scenario03/api/feedback/submit", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username: userName, content: content })
        })
        .then(function (r) { return r.text(); })
        .then(function (html) {
            $("#feedbackPanel").html(html);
            loadLogs();
        });
    });

    function loadLogs() {
        fetch("/scenario07/admin/logs/search?q=")
            .then(function (r) { return r.json(); })
            .then(function (logs) {
                var html = "<table class='log-table'><tbody>";
                logs.slice(0, 5).forEach(function (log) {
                    html += "<tr><td>" + log.action + "</td><td>" + log.user
                        + "</td><td>" + log.message + "</td><td>" + log.detail + "</td></tr>";
                });
                html += "</tbody></table>";
                $("#logsPanel").html(html);
            });
    }

    $("#refreshLogsBtn").on("click", loadLogs);

    if (urlParams.q) {
        $("#searchBtn").click();
    }
    loadLogs();
})(jQuery);
