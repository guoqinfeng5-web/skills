<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head><title>Error (Safe)</title></head>
<body>
<h2>Error</h2>
<%--
  Safe: use fn:escapeXml to escape the error message.
  Vulnerable version used: <%= request.getAttribute("errorMsg") %>
--%>
<p><c:out value=""/></p>
<a href="login.jsp">Back to Login</a>
</body>
</html>
