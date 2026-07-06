window.UrlParams = {
    getParams: function () {
        const params = {};
        const search = location.search.substring(1);
        if (!search) return params;
        search.split("&").forEach(function (pair) {
            const parts = pair.split("=");
            params[decodeURIComponent(parts[0])] = decodeURIComponent(parts[1] || "");
        });
        return params;
    }
};
