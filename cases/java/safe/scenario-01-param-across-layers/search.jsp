<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head><title>Search Result (Safe)</title></head>
<body>
<h2>Search Result</h2>
<%--
  Safe: Use <c:out> to escape HTML in displayContent.
  Vulnerable version used  directly.
--%>
<p><c:out value=""/></p>
</body>
</html>
