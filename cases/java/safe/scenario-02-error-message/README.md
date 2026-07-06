# Scenario 02 — Error Message XSS (Safe)

## Vulnerability
error.jsp used <%= request.getAttribute("errorMsg") %> which emitted unescaped HTML,
allowing an attacker to inject malicious script via the error message.

## Fix
- AuthController.java: Used HtmlUtils.htmlEscape() on the error message before adding it to the model.
- error.jsp: Used <c:out value="\"/> (JSTL) instead of raw scriptlet output.
- login.jsp: No changes needed, kept for structural consistency.
