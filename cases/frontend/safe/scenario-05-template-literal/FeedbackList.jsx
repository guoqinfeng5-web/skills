import React, { useState, useEffect } from "react";
import FeedbackItem from "./FeedbackItem";
import { fetchFeedback } from "./feedbackUtils";

const FeedbackList = () => {
  const [feedbackList, setFeedbackList] = useState([]);

  useEffect(() => {
    fetchFeedback().then(setFeedbackList);
  }, []);

  return (
    <div className="feedback-list">
      <h1>用户反馈</h1>
      {feedbackList.map((item) => (
        <FeedbackItem key={item.id} feedback={item} />
      ))}
    </div>
  );
};

export default FeedbackList;
