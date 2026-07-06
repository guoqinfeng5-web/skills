/**
 * 安全地从 URL hash 中读取指定参数值
 * 返回纯文本，不做 HTML 解析
 */
export function getHashParam(paramName) {
  const hash = window.location.hash.substring(1); // 移除 #
  const params = new URLSearchParams(hash);
  const value = params.get(paramName);
  return value || "";
}

/**
 * 安全地构建 hash 字符串
 */
export function buildHash(params) {
  const searchParams = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    searchParams.set(key, value);
  });
  return "#" + searchParams.toString();
}
