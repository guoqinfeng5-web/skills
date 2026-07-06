(function () {
    const params = new URLSearchParams(location.search);
    const userName = params.get("user") || "测试用户";
    const amount = params.get("amount") || "99.00";

    window.addEventListener("load", function () {
        const iframe = document.getElementById("paymentFrame");
        iframe.contentWindow.postMessage({
            type: "payment",
            userName: userName,
            amount: amount
        }, "*");
    });
})();
