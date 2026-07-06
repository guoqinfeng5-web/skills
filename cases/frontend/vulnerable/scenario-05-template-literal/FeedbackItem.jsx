import React from "react";
import { formatFeedbackContent } from "./feedbackUtils";

const FeedbackItem = ({ data }) => {
  const htmlContent = formatFeedbackContent(data.content);

  return (
    <div className="feedback-item">
      <div className="feedback-header">
        <span className="author">{data.author}</span>
        <span className="date">{data.createdAt}</span>
      </div>
      <div
        className="feedback-body"
        dangerouslySetInnerHTML={{ __html: htmlContent }}
      />
      <div className="feedback-footer">
        <button onClick={() => handleReply(data.id)}>回复</button>
        <button onClick={() => handleLike(data.id)}>赞 ({data.likes})</button>
      </div>
    </div>
  );
};

function handleReply(id) {
  console.log("Reply to:", id);
}

function handleLike(id) {
  console.log("Like:", id);
}

export default FeedbackItem;
