# Scenario 07 — Log Reflection XSS (Safe)

## Vulnerability
Vulnerable version rendered raw log content directly in JSP with ${logContent}.
If an attacker could inject content into logs (e.g., via username or upload filename),
the script would execute in the admin's browser.

## Fix
- LogController.java: Used HtmlUtils.htmlEscape() on the full log content before adding it to the model.
- dmin.jsp: Used <c:out value="\"/> for safe output.
- log-viewer.js: Uses 	extContent (safe) instead of innerHTML.
- LogService.java: Returns raw data; escaping is the controller's responsibility.
