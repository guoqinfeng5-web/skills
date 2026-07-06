<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head><title>Upload Result (Safe)</title></head>
<body>
<h2>File Uploaded Successfully</h2>
<%--
  Safe: <c:out> escapes the filename, preventing XSS via malicious file names.
  Vulnerable version used  directly.
--%>
<p>File: <c:out value=""/></p>
<p>Saved to: <c:out value=""/></p>
<a href="upload.jsp">Upload another</a>
</body>
</html>
