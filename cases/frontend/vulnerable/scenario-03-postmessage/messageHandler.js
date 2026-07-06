/**
 * 格式化支付消息为 HTML
 * 注意：此函数直接拼接 HTML，存在注入风险
 */
function formatPaymentMessage(userName, amount) {
  const formattedAmount = parseFloat(amount).toFixed(2);
  const now = new Date().toLocaleString("zh-CN");

  // 模板字符串拼接 HTML —— 用户输入 userName 未转义
  return `
    <div class="payment-card">
      <h3 class="success">支付成功 ✓</h3>
      <p><strong>用户：</strong>${userName}</p>
      <p><strong>金额：</strong>¥${formattedAmount}</p>
      <p><strong>时间：</strong>${now}</p>
    </div>
  `;
}

/**
 * 格式化错误消息
 */
function formatErrorMessage(code, detail) {
  return `<div class="error-card"><p>错误 ${code}: ${detail}</p></div>`;
}
