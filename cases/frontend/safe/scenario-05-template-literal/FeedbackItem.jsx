import React from "react";
import DOMPurify from "dompurify";

const FeedbackItem = ({ feedback }) => {
  // 修复方案 1：纯文本内容直接使用 JSX {} 插值（自动转义）
  const renderTextContent = () => {
    return <p>{feedback.content}</p>;
  };

  // 修复方案 2：如需渲染受信任的富文本，使用 DOMPurify 消毒
  const renderRichContent = () => {
    const sanitized = DOMPurify.sanitize(feedback.content, {
      ALLOWED_TAGS: ["b", "i", "em", "strong", "a", "p", "br"],
      ALLOWED_ATTR: ["href"],
    });
    return <p dangerouslySetInnerHTML={{ __html: sanitized }} />;
  };

  return (
    <div className="feedback-item">
      <h3>
        {/* JSX {} 自动转义 HTML 字符 */}
        来自: {feedback.author}
      </h3>
      <p className="rating">评分: {"★".repeat(feedback.rating)}</p>
      {feedback.allowHtml ? renderRichContent() : renderTextContent()}
    </div>
  );
};

export default FeedbackItem;
