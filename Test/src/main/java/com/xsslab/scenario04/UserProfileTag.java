package com.xsslab.scenario04;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class UserProfileTag extends TagSupport {

    private static final long serialVersionUID = 1L;

    private String nickName;
    private String avatar;
    private String level;

    @Override
    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            out.println("<div class=\"user-profile-card\">");
            out.println("  <div class=\"user-avatar\">");

            if (avatar != null && !avatar.isEmpty()) {
                out.print("    <img src=\"" + avatar + "\" alt=\"" + nickName + "\"/>");
            } else {
                out.print("    <div class=\"avatar-placeholder\">" + nickName + "</div>");
            }

            out.println("  </div>");
            out.println("  <div class=\"user-info\">");
            out.print("    <span class=\"user-nickname\">" + nickName + "</span>");
            out.println("  </div>");

            if (level != null && !level.isEmpty()) {
                out.println("  <div class=\"user-level\">");
                out.print("    <span class=\"level-badge\">" + level + "</span>");
                out.println("  </div>");
            }

            out.println("</div>");
        } catch (IOException e) {
            throw new JspException("输出用户资料标签时出错", e);
        }
        return SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            if (nickName != null && nickName.contains("[V]")) {
                out.print("<span class=\"verified-badge\">" + nickName + "</span>");
            }
        } catch (IOException e) {
            throw new JspException("输出认证标记时出错", e);
        }
        return EVAL_PAGE;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
