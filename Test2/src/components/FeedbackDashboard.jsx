// File: src/components/FeedbackDashboard.jsx
import React, { useEffect, useState } from 'react';

export default function FeedbackDashboard() {
    const [feedbacks, setFeedbacks] = useState([]);

    useEffect(() => {
        // 获取后端反馈数据
        fetch('/api/feedback/list')
            .then(res => res.json())
            .then(data => setFeedbacks(data));
    }, []);

    // 解析当前 URL 参数
    const urlParams = new URLSearchParams(window.location.search);
    
    // 业务 4：根据 URL 参数决定返回上一页的路径
    const returnUrl = urlParams.get("returnUrl") || "/home";
    
    // 业务 5：根据 URL 参数切换看板主题颜色
    const highlightTheme = urlParams.get("theme") || "default-theme";

    return (
        <div className={`dashboard theme-${highlightTheme}`}>
            <header>
                <h2>用户反馈看板</h2>
                {/* 返回按钮 */}
                <a href={returnUrl} className="back-link">返回上一页</a>
            </header>

            <main className="feedback-list">
                {feedbacks.map(fb => (
                    <div key={fb.id} className="feedback-item">
                        <h3>{fb.title}</h3>
                        {/* 业务要求：反馈内容支持富文本展示 */}
                        <div 
                            className="content-body" 
                            dangerouslySetInnerHTML={{ __html: fb.content }} 
                        />
                    </div>
                ))}
            </main>
        </div>
    );
}