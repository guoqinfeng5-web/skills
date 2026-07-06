/**
 * 对 hash 路由中的参数进行解码
 * 支持处理 + 号、% 编码等
 */
export function decodeHashParam(value) {
  if (!value) return "";
  try {
    // 先替换 + 号为空格，再 URL 解码
    const decoded = decodeURIComponent(value.replace(/\+/g, " "));
    return decoded;
  } catch (e) {
    return value;
  }
}

/**
 * 从 hash 中解析完整的 query 对象
 * 兼容不支持 URLSearchParams 的旧浏览器
 */
export function parseHashQuery(hash) {
  const queryIndex = hash.indexOf("?");
  if (queryIndex === -1) return {};
  const queryString = hash.substring(queryIndex + 1);
  const params = {};
  queryString.split("&").forEach((pair) => {
    const [key, val] = pair.split("=");
    if (key) {
      params[decodeURIComponent(key)] = val ? decodeHashParam(val) : "";
    }
  });
  return params;
}
