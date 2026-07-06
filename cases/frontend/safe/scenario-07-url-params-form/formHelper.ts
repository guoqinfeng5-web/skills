/**
 * 安全地从 URL 参数中读取表单预填数据
 */

/**
 * 从 URL 查询参数中读取表单字段
 */
export function getFormParamsFromURL(): Record<string, string> {
  const params = new URLSearchParams(window.location.search);
  const result: Record<string, string> = {};

  // 只读取预定义的字段名
  const allowedFields = ["title", "content", "color"];

  allowedFields.forEach((field) => {
    const value = params.get(field);
    if (value !== null) {
      result[field] = value;
    }
  });

  return result;
}

/**
 * 转义文本中的 HTML 特殊字符
 */
export function sanitizeText(input: string): string {
  return input
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#x27;");
}

/**
 * 消毒富文本内容（仅允许安全的标签和属性）
 * 当表单支持富文本编辑器时使用
 */
export function sanitizeHtml(input: string): string {
  // 使用 DOMPurify 消毒（生产环境）
  // return DOMPurify.sanitize(input);

  // 在没有 DOMPurify 的轻量场景，使用白名单过滤
  return input
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#x27;");
}

/**
 * 安全地构建表单提交数据
 */
export function buildSafeFormData(data: Record<string, string>): Record<string, string> {
  const safe: Record<string, string> = {};
  Object.entries(data).forEach(([key, value]) => {
    safe[key] = typeof value === "string" ? sanitizeText(value) : String(value);
  });
  return safe;
}
