/**
 * 将纯文本反馈内容转换为带简单标记的 HTML。
 * 支持：
 *  -  **粗体** → <strong>粗体</strong>
 *  -  *斜体*  → <em>斜体</em>
 *  -  换行   → <br/>
 *
 * 注意：函数内部使用模板字符串直接拼接输入，未做转义。
 */

const BOLD_RE = /\*\*(.+?)\*\*/g;
const ITALIC_RE = /\*(.+?)\*/g;

export function formatFeedbackContent(text) {
  if (!text) return "";

  // 先对特殊字符做一次转义（意图是防 XSS，但只做了部分）
  const escaped = text
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;");

  // 关键问题：这里用模板字符串拼接，替换回调中直接插入了匹配组，
  // 但如果 BOLD_RE 匹配到的内容包含 HTML 标签（用户输入 <img src=x>），
  // 前面的 escape 虽然转了 < >，但替换后的 <strong> 是安全的。
  //
  // 真正的漏洞在于：text 中的内容经过下面一系列 replace 后，
  // 最终生成的 HTML 字符串里，用户输入的未预期内容（如事件处理器）可能被保留。
  // 更直接的是——如果输入不带 ** 或 *，则下面的 replace 不匹配，
  // 但 escaped 已经被部分转义，然而还有 escape 不完善的地方。

  let html = escaped;
  html = html.replace(BOLD_RE, "<strong>$1</strong>");
  html = html.replace(ITALIC_RE, "<em>$1</em>");
  html = html.replace(/\n/g, "<br/>");

  // 额外功能：将 URL 转为可点击链接 — 这里用模板字符串直接拼接！
  html = html.replace(
    /(https?:\/\/[^\s<]+)/g,
    `<a href="$1" target="_blank">$1</a>`
  );

  return html;
}

/**
 * 截取反馈摘要（也使用模板字符串）
 */
export function truncateContent(text, maxLen = 100) {
  const plain = text.replace(/<[^>]*>/g, "");
  if (plain.length <= maxLen) return plain;
  return `${plain.substring(0, maxLen)}...`;
}
