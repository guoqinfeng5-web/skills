import React, { useState, useEffect } from "react";
import FeedbackItem from "./FeedbackItem";

const API = "/api/feedback";

const FeedbackList = ({ category }) => {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    fetch(`${API}?cat=${category}`)
      .then((res) => res.json())
      .then((data) => {
        setItems(data);
        setLoading(false);
      });
  }, [category]);

  if (loading) return <div>加载中...</div>;

  return (
    <div className="feedback-list">
      <h2>用户反馈 ({items.length})</h2>
      {items.map((item) => (
        <FeedbackItem key={item.id} data={item} />
      ))}
    </div>
  );
};

export default FeedbackList;
