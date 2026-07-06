# Scenario 05 — File Upload XSS (Safe)

## Vulnerability
Vulnerable version put the original filename (which can contain HTML/script) directly into the model,
and JSP rendered it with ${fileName} without escaping.

## Fix
- FileUploadController.java: Used HtmlUtils.htmlEscape() on the filename before setting it in the model.
- uploadResult.jsp: Used <c:out value="\"/> for safe rendering.
- FileService.java: Receives and uses the already-escaped filename.
