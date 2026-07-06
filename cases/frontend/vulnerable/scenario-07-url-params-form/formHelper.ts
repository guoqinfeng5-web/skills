/**
 * 表单预填充工具
 * 从 URL 查询参数中读取 base64 编码的内容并解码
 */

export function parsePrefillData(query) {
  if (!query) return null;

  try {
    const title = query.title || "";

    // content 参数是 base64 编码的富文本内容
    let content = "";
    if (query.content) {
      content = atob(decodeURIComponent(query.content));
    }

    // note 参数也是 base64 编码
    let note = "";
    if (query.note) {
      note = atob(decodeURIComponent(query.note));
    }

    return { title, content, note };
  } catch (e) {
    console.warn("解析预填充数据失败:", e);
    return null;
  }
}

/**
 * 将表单数据编码为 URL 查询参数（用于分享编辑链接）
 */
export function encodeFormData(title, content, note) {
  const params = new URLSearchParams();
  if (title) params.set("title", title);
  if (content) params.set("content", btoa(content));
  if (note) params.set("note", btoa(note));
  return params.toString();
}
