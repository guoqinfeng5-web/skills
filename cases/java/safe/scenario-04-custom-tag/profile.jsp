<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/tlds/user.tld" prefix="safe" %>
<html>
<head><title>User Profile (Safe)</title></head>
<body>
<h2>User Profile</h2>
<%--
  Safe: the custom tag internally uses HtmlUtils.htmlEscape().
  Vulnerable version wrote the attribute directly without escaping.
--%>
<safe:userProfile displayName=""/>
</body>
</html>
