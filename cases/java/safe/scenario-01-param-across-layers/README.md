# Scenario 01 — Parameter Passing Across Layers (Safe)

## Vulnerability
Controller passed raw user input directly through Service and Wrapper layers to JSP.
JSP used ${result.displayContent} which rendered unescaped HTML.

## Fix
- SearchController.java: HTML-escaped input with HtmlUtils.htmlEscape() before passing downstream.
- search.jsp: Used <c:out value="..."/> to ensure output is safely escaped.
- Data flow: raw input → escped at Controller → Service/Wrapper pass through → JSP <c:out>.
