function formatPaymentMessage(userName, amount) {
    return "<div class=\"payment-msg\">"
        + "<p>付款人: <strong>" + userName + "</strong></p>"
        + "<p>金额: ¥" + amount + "</p>"
        + "</div>";
}

(function () {
    const container = document.getElementById("payment-notification");

    window.addEventListener("message", function (event) {
        if (!event.origin.includes("localhost") && !event.origin.includes("127.0.0.1")) return;

        const data = event.data;
        if (!data || data.type !== "payment") return;

        container.innerHTML = formatPaymentMessage(data.userName, data.amount);
    });
})();
