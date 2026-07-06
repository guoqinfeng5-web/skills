<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head><title>Admin — Logs (Safe)</title></head>
<body>
<h2>System Logs</h2>
<form method="get">
    Filter: <input type="text" name="filter"/>
    <input type="submit" value="Search"/>
</form>
<hr/>
<pre>
<%--
  Safe: <c:out> escapes the log content, preventing XSS if logs contain HTML/script.
  Vulnerable version used  directly.
--%>
<c:out value=""/>
</pre>
<script src="log-viewer.js"></script>
</body>
</html>
