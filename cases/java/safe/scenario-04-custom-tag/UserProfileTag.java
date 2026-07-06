package com.xss.safe.scenario04;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspWriter;
import org.springframework.web.util.HtmlUtils;

/**
 * Safe version: HTML-escapes the displayName attribute before output.
 */
public class UserProfileTag extends TagSupport {

    private String displayName;

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public int doStartTag() throws JspException {
        try {
            JspWriter out = pageContext.getOut();
            // Safe: escape HTML before writing
            String safe = HtmlUtils.htmlEscape(displayName);
            out.write("<span class=\"user-profile\">" + safe + "</span>");
        } catch (Exception e) {
            throw new JspException(e.getMessage(), e);
        }
        return SKIP_BODY;
    }
}
