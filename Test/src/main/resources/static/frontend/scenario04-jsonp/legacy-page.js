(function () {
    const params = new URLSearchParams(location.search);
    const cbName = params.get("cb") || params.get("callback") || "handleUsers";

    window[cbName] = function (data) {
        const container = document.getElementById("userList");
        let html = "<ul>";
        data.forEach(function (user) {
            html += "<li>" + user.name + " (ID: " + user.id + ")</li>";
        });
        html += "</ul>";
        container.innerHTML = html;
    };

    const script = document.createElement("script");
    script.src = "/scenario04/api/jsonp?callback=" + encodeURIComponent(cbName);
    document.body.appendChild(script);
})();
