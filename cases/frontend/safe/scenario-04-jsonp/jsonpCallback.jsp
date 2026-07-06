<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8" />
  <title>JSONP 回调处理</title>
</head>
<body>
  <h1>JSONP 请求处理</h1>
  <div id="result"></div>

  <script>
    // 修复：使用白名单校验 callback 函数名
    function isValidCallback(name) {
      return /^[a-zA-Z_][a-zA-Z0-9_.]*$/.test(name);
    }

    (function () {
      var params = new URLSearchParams(window.location.search);
      var callbackName = params.get("callback") || "handleData";

      if (!isValidCallback(callbackName)) {
        document.getElementById("result").textContent =
          "错误：无效的 callback 参数";
        return;
      }

      // 仅当校验通过后才创建 script 标签
      var script = document.createElement("script");
      script.src = "/api/jsonp?callback=" + encodeURIComponent(callbackName);
      document.body.appendChild(script);
    })();

    function handleData(data) {
      var output = document.getElementById("result");
      output.textContent = "收到数据: " + JSON.stringify(data);
    }
  </script>
</body>
</html>
