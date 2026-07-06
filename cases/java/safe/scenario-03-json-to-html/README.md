# Scenario 03 — JSON to HTML (Safe)

## Vulnerability
Vulnerable version built an HTML string on the server (by concatenating user input)
and returned it as 	ext/html, which rendered unescaped script tags.

## Fix
- FeedbackController.java: Changed to @RestController returning JSON. Input is HTML-escaped.
- eedback.js: Uses 	extContent (safe) instead of innerHTML to display server response.
- eedback.html: Kept as static HTML; all rendering is DOM-based and safe.
