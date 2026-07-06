function submitFeedback() {
    const username = document.getElementById("username").value;
    const content = document.getElementById("content").value;

    fetch("/scenario03/api/feedback/submit", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, content })
    })
    .then(response => response.text())
    .then(html => {
        document.getElementById("feedbackList").innerHTML += html;
    });
}

function loadFeedbackList(page) {
    fetch("/scenario03/api/feedback/list?page=" + (page || 1))
    .then(response => response.text())
    .then(html => {
        $("#feedbackList").html(html);
    });
}

document.addEventListener("DOMContentLoaded", function() {
    document.getElementById("submitBtn").addEventListener("click", submitFeedback);
    loadFeedbackList(1);
});
