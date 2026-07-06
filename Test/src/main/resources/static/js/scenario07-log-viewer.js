function searchLogs() {
    var keyword = document.getElementById("searchKeyword").value;

    fetch("/scenario07/admin/logs/search?q=" + encodeURIComponent(keyword))
    .then(response => response.json())
    .then(logs => {
        var tbody = document.querySelector(".log-table tbody");
        tbody.innerHTML = "";

        logs.forEach(function(log) {
            var rowHtml = `
                <tr>
                    <td>${log.id}</td>
                    <td>${log.level}</td>
                    <td>${log.action}</td>
                    <td>${log.user}</td>
                    <td class="log-message">${log.message}</td>
                    <td class="log-detail">${log.detail}</td>
                    <td>${log.formattedTime}</td>
                </tr>
            `;
            tbody.innerHTML += rowHtml;
        });
    });
}

function recordLog() {
    var message = document.getElementById("logMessage").value;
    var detail = document.getElementById("logDetail").value;
    var body = "message=" + encodeURIComponent(message) + "&detail=" + encodeURIComponent(detail);

    fetch("/scenario07/admin/logs/record", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: body
    }).then(function() {
        window.location.reload();
    });
}

document.addEventListener("DOMContentLoaded", function() {
    document.getElementById("searchBtn").addEventListener("click", searchLogs);
    document.getElementById("recordBtn").addEventListener("click", recordLog);
});
