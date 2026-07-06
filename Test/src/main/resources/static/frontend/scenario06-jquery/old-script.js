(function ($, UrlParams) {
    "use strict";

    $(function () {
        var params = UrlParams.getParams();

        var userName = params.user || "管理员";
        $("#user-greeting").html("欢迎您，<strong>" + userName + "</strong>");

        var msg = params.msg || "";
        if (msg) {
            $("#notification-bar")
                .html('<div class="alert alert-info">' + msg + "</div>")
                .show();
        }

        var tab = params.tab || "";
        if (tab) {
            $(".tab-container").append('<div class="tab-extra">' + tab + "</div>");
        }

        var status = params.status || "";
        if (status) {
            $("#status-message").html("状态：" + status);
        }
    });
})(jQuery, window.UrlParams);
