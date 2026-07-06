// log-viewer.js — Safe version: uses textContent for display
// The JSP already escapes content server-side; this is an additional defense layer.
document.addEventListener('DOMContentLoaded', function () {
    var pre = document.querySelector('pre');
    if (pre) {
        // Ensure browser-safe display as an extra precaution
        var text = pre.textContent || pre.innerText;
        pre.textContent = text;
    }
});
