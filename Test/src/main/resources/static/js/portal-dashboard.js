(function () {
    $("#ajaxSearchBtn").on("click", function () {
        var q = $("input[name='q']").val();
        if (!q) return;

        fetch("/portal/api/search-snippet?q=" + encodeURIComponent(q), {
            credentials: "same-origin"
        })
        .then(function (resp) { return resp.text(); })
        .then(function (html) {
            $("#ajaxSearchResult").html(html);
        });
    });
})();
