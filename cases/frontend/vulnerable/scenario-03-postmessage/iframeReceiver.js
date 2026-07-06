/**
 * iframe 中接收 postMessage 的处理脚本
 */
(function () {
  const container = document.getElementById("payment-notification");

  window.addEventListener("message", function (event) {
    if (!event.origin.includes("example.com")) return;

    const data = event.data;
    if (!data || data.type !== "payment") return;

    const html = formatPaymentMessage(data.userName, data.amount);
    container.innerHTML = html;
  });
})();
