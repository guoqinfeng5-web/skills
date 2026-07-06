/**
 * 安全的消息接收器
 * - 严格验证消息来源 origin
 * - 使用 textContent 替代 innerHTML
 * - 仅接受 JSON 格式的受控数据
 */

const ALLOWED_ORIGINS = [
  "https://trusted-parent.example.com",
  "https://www.example.com",
];

function isOriginAllowed(origin) {
  return ALLOWED_ORIGINS.includes(origin);
}

function handleMessage(event) {
  // 1. 验证 origin 白名单
  if (!isOriginAllowed(event.origin)) {
    console.warn("[安全] 来自未知来源的消息已忽略:", event.origin);
    return;
  }

  // 2. 验证数据类型
  let data;
  try {
    data = typeof event.data === "string" ? JSON.parse(event.data) : event.data;
  } catch (e) {
    console.warn("[安全] 非 JSON 格式消息已忽略");
    return;
  }

  // 3. 验证数据结构
  if (!data || typeof data !== "object" || !data.type) {
    console.warn("[安全] 无效的消息结构");
    return;
  }

  // 4. 安全地渲染内容
  const output = document.getElementById("message-output");
  if (!output) return;

  switch (data.type) {
    case "greeting":
      // 使用 textContent 安全填充
      output.textContent = "你好, " + (data.name || "访客") + "!";
      break;

    case "notification":
      output.textContent = data.message || "";
      break;

    default:
      output.textContent = "收到消息类型: " + data.type;
  }
}

window.addEventListener("message", handleMessage);
