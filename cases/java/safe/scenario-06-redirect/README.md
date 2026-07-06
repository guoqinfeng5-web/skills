# Scenario 06 — Open Redirect XSS (Safe)

## Vulnerability
Vulnerable version accepted an arbitrary edirect parameter and passed it directly to
sendRedirect() or edirect: prefix, enabling open redirect attacks.

## Fix
- LoginController.java: Added alidateAndEncodeRedirect() with a whitelist of allowed paths;
  uses UriComponentsBuilder for safe encoding.
- RedirectController.java: Added alidateUrl() that rejects external domains and path traversal.
- login.jsp: Uses <c:out> for safe display of error messages.
