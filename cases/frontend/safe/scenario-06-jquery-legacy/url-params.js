/**
 * 安全地读取 URL 查询参数
 * 修复：使用 .text() 和 createTextNode 替代 .html()
 */
$(function () {
  // 安全的参数读取函数
  function getQueryParam(name) {
    var params = new URLSearchParams(window.location.search);
    return params.get(name) || "";
  }

  // 安全方案 1：使用 .text()
  var msg = getQueryParam("message");
  if (msg) {
    $("#url-params-content").text(decodeURIComponent(msg));
  }

  // 安全方案 2：使用原生 DOM API（适用于需要部分富文本的场景）
  var title = getQueryParam("title");
  if (title) {
    var decodedTitle = decodeURIComponent(title);

    // 先创建一个 span 存放预处理后的安全 HTML（经过消毒）
    var $titleEl = $("<h3>");
    $titleEl.text(decodedTitle); // .text() 自动转义
    $("#url-params-content").prepend($titleEl);
  }

  // 安全方案 3：如需少量允许的 HTML，使用正则消毒
  var safeHtml = getQueryParam("safe_html");
  if (safeHtml) {
    var decoded = decodeURIComponent(safeHtml);
    // 只允许 <b>, <i>, <em> 标签，移除其他所有标签
    var sanitized = decoded
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/&lt;(\/?(?:b|i|em))&gt;/gi, "<$1>");

    $("#url-params-content").append(sanitized);
  }
});
