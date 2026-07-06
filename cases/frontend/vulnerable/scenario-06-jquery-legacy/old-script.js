/**
 * 后台管理页面主脚本
 */
(function ($, UrlParams) {
  "use strict";

  $(function () {
    // 从 URL 中获取参数
    var params = UrlParams.getParams();

    // 渲染用户问候语 — 从 URL 获取用户名
    var userName = params.user || "管理员";
    $("#user-greeting").html("欢迎您，<strong>" + userName + "</strong>");

    // 渲染通知栏消息 — 从 URL 获取消息内容
    var msg = params.msg || "";
    if (msg) {
      $("#notification-bar")
        .html('<div class="alert alert-info">' + msg + "</div>")
        .show();
    }

    // 根据 page 参数加载不同内容
    var page = params.page || "dashboard";
    $("#page-content").load("/pages/" + page + ".html", function () {
      // 加载完成后，如果有 tab 参数，激活对应标签
      var tab = params.tab || "";
      if (tab) {
        $(".tab-container").append(
          '<div class="tab-extra">' + tab + "</div>"
        );
      }
    });

    // 状态栏信息
    var status = params.status || "";
    if (status) {
      $("#status-message").html("状态：" + status);
    }
  });
})(jQuery, window.UrlParams);
