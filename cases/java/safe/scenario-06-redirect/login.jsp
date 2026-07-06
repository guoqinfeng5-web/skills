<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head><title>Login (Safe)</title></head>
<body>
<h2>Login</h2>
<c:if test="">
    <p style="color:red"><c:out value=""/></p>
</c:if>
<form method="post" action="login">
    Username: <input type="text" name="username"/><br/>
    Password: <input type="password" name="password"/><br/>
    <input type="text" name="redirect" placeholder="Redirect (optional)"/><br/>
    <input type="submit" value="Login"/>
</form>
</body>
</html>
