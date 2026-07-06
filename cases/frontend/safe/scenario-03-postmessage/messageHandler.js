/**
 * 安全的消息发送器
 * - 明确指定 targetOrigin
 * - 仅发送允许的消息类型
 * - 数据序列化发送
 */

const ALLOWED_MESSAGE_TYPES = ["greeting", "notification"];

export function sendMessageToIframe(iframeWindow, targetOrigin, data) {
  if (!ALLOWED_MESSAGE_TYPES.includes(data.type)) {
    console.warn("[安全] 不允许发送此消息类型:", data.type);
    return;
  }

  // 序列化为 JSON，防止原型污染
  const safeData = JSON.parse(JSON.stringify(data));

  iframeWindow.postMessage(safeData, targetOrigin);
}

export function sendMessageToParent(targetOrigin, data) {
  if (!ALLOWED_MESSAGE_TYPES.includes(data.type)) {
    console.warn("[安全] 不允许发送此消息类型:", data.type);
    return;
  }

  const safeData = JSON.parse(JSON.stringify(data));

  window.parent.postMessage(safeData, targetOrigin);
}
