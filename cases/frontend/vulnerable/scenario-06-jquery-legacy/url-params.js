/**
 * 前端 URL 参数解析工具
 * 兼容不支持 URLSearchParams 的旧浏览器
 */
(function (root) {
  "use strict";

  function getParams() {
    var search = root.location.search;
    if (!search) return {};

    var params = {};
    var pairs = search.substring(1).split("&");

    for (var i = 0; i < pairs.length; i++) {
      var pair = pairs[i];
      if (!pair) continue;

      var eqIndex = pair.indexOf("=");
      var key, value;

      if (eqIndex === -1) {
        key = decodeURIComponent(pair);
        value = "";
      } else {
        key = decodeURIComponent(pair.substring(0, eqIndex));
        value = decodeURIComponent(pair.substring(eqIndex + 1));
      }

      params[key] = value;
    }

    return params;
  }

  root.UrlParams = {
    getParams: getParams,
    get: function (name) {
      return getParams()[name] || null;
    },
  };
})(window);
