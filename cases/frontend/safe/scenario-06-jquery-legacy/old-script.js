/**
 * 安全版留言本脚本
 * 修复：使用 .text() 替代 .html()，防止 XSS
 */

$(function () {
  // 安全地渲染留言列表
  function renderComments(comments) {
    var $list = $("#comment-list");
    $list.empty();

    $.each(comments, function (i, comment) {
      var $div = $("<div>").addClass("comment-item");

      // 安全方案 1：使用 .text() 设置文本（自动转义 HTML）
      $("<strong>").text(comment.author).appendTo($div);
      $div.append(document.createTextNode(" 说："));
      $("<p>").text(comment.content).appendTo($div);
      $("<small>").text(comment.time).appendTo($div);

      $list.append($div);
    });
  }

  // 模拟数据
  var mockComments = [
    { author: "用户A", content: "这是一条 <b>正常</b> 留言", time: "2026-07-03" },
    { author: "用户B", content: "安全测试 <img src=x onerror=alert(1)>", time: "2026-07-03" },
  ];

  renderComments(mockComments);

  // 提交留言（安全处理）
  $("#comment-form").on("submit", function (e) {
    e.preventDefault();

    var newComment = {
      author: $("#username").val() || "匿名",
      content: $("#comment-text").val(),
      time: new Date().toISOString().slice(0, 10),
    };

    // 添加到列表
    renderComments(mockComments.concat([newComment]));

    // 清空输入
    $("#username, #comment-text").val("");
  });
});
