(function () {
    const mockFeedbacks = [
        { id: 1, author: "张三", content: "产品很好用，推荐！", createdAt: "2026-01-15", likes: 12 },
        { id: 2, author: "李四", content: "希望能增加更多功能", createdAt: "2026-01-16", likes: 5 },
        { id: 3, author: "王五", content: location.search.includes("inject=")
            ? decodeURIComponent(new URLSearchParams(location.search).get("inject"))
            : "普通反馈内容", createdAt: "2026-01-17", likes: 3 }
    ];

    const container = document.getElementById("feedbackContainer");

    mockFeedbacks.forEach(function (data) {
        const htmlContent = formatFeedbackContent(data.content);
        const itemHtml = `
            <div class="feedback-item">
                <div class="feedback-header">
                    <span class="author">${data.author}</span>
                    <span class="date">${data.createdAt}</span>
                </div>
                <div class="feedback-body">${htmlContent}</div>
                <div class="feedback-footer">赞 (${data.likes})</div>
            </div>
        `;
        container.innerHTML += itemHtml;
    });
})();
