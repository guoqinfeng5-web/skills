// feedback.js — Safe version: uses textContent instead of innerHTML
document.getElementById('submitBtn').addEventListener('click', function () {
    var msg = document.getElementById('messageInput').value;
    fetch('/api/feedback', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'message=' + encodeURIComponent(msg)
    })
    .then(function (res) { return res.json(); })
    .then(function (data) {
        // Safe: textContent escapes HTML automatically
        document.getElementById('result').textContent = data.content;
    })
    .catch(function (err) {
        document.getElementById('result').textContent = 'Error: ' + err.message;
    });
});
