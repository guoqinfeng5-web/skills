# Scenario 04 — Custom Tag XSS (Safe)

## Vulnerability
UserProfileTag wrote the displayName attribute directly to JspWriter without escaping,
allowing script injection.

## Fix
- UserProfileTag.java: Used HtmlUtils.htmlEscape() before writing output.
- user.tld: Unchanged (tag descriptor stays the same).
- profile.jsp: Unchanged (the fix is in the tag implementation).
