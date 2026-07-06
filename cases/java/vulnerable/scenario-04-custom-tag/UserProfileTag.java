package com.example.profile.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

/**
 * 自定义 JSP 标签 —— 用于渲染用户头像和昵称
 * 
 * 注意: 此标签直接使用 out.print() 输出属性值，没有做 HTML 转义
 * 开发者使用此标签时传入的属性值如果包含用户输入，将存在 XSS 风险
 */
public class UserProfileTag extends TagSupport {

    private static final long serialVersionUID = 1L;

    /**
     * 用户昵称（标签属性）
     */
    private String nickName;

    /**
     * 用户头像 URL（标签属性）
     */
    private String avatar;

    /**
     * 用户等级（标签属性）
     */
    private String level;

    /**
     * 标签开始处理 —— 直接输出 HTML
     */
    @Override
    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            // ---- 输出用户信息 HTML 片段 ----
            // 漏洞点: nikeName 和 level 直接通过 out.print() 输出
            // 如果属性值包含恶意 HTML/JS，将直接注入到页面中
            out.println("<div class=\"user-profile-card\">");
            out.println("  <div class=\"user-avatar\">");

            // 输出头像
            if (avatar != null && !avatar.isEmpty()) {
                out.print("    <img src=\"" + avatar + "\" alt=\"" + nickName + "\"/>");
            } else {
                out.print("    <div class=\"avatar-placeholder\">" + nickName + "</div>");
            }

            out.println("  </div>");
            out.println("  <div class=\"user-info\">");
            out.print("    <span class=\"user-nickname\">" + nickName + "</span>");  // 未转义！
            out.println("  </div>");

            // 如果设置了等级，也直接输出
            if (level != null && !level.isEmpty()) {
                out.println("  <div class=\"user-level\">");
                out.print("    <span class=\"level-badge\">" + level + "</span>");  // 未转义！
                out.println("  </div>");
            }

            out.println("</div>");

        } catch (IOException e) {
            throw new JspException("输出用户资料标签时出错", e);
        }

        return SKIP_BODY; // 空标签，不处理标签体
    }

    /**
     * 标签结束处理
     */
    @Override
    public int doEndTag() throws JspException {
        // 某些情况下也会输出额外信息
        JspWriter out = pageContext.getOut();
        try {
            // 如果昵称中有特殊字符，这里还输出一个"已认证"标记
            // 注意: 这里也直接拼接了用户输入
            if (nickName != null && nickName.contains("[V]")) {
                out.print("<span class=\"verified-badge\">" + nickName + "</span>");  // 未转义！
            }
        } catch (IOException e) {
            throw new JspException("输出认证标记时出错", e);
        }
        return EVAL_PAGE;
    }

    // ---- 标签属性 setter 方法（由 JSP 引擎通过反射调用） ----

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
