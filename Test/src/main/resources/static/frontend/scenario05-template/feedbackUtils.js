function formatFeedbackContent(content) {
    if (!content) return "";
    let escaped = content
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;");

    const urlRegex = /(https?:\/\/[^\s]+)/g;
    escaped = escaped.replace(urlRegex, '<a href="$1" target="_blank">$1</a>');

    return `<div class="feedback-text">${escaped}</div>`;
}

window.formatFeedbackContent = formatFeedbackContent;
