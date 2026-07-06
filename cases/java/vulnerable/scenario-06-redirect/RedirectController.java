package com.example.redirect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 重定向控制器
 * 
 * 处理各种业务场景中的 URL 重定向操作
 */
@Controller
@RequestMapping("/redirect")
public class RedirectController {

    /**
     * 外部链接跳转 — 某些业务场景需要跳转到用户指定的 URL
     * 
     * @param url    目标 URL（用户输入）
     * @param response HTTP 响应
     */
    @GetMapping("/external")
    public void redirectToExternal(
            @RequestParam("url") String url,
            HttpServletResponse response) {

        // ---- 漏洞点: 直接使用 sendRedirect 跳转到用户输入的 URL ----
        // url 参数来自用户输入，未做白名单校验
        // 攻击者可以构造:
        //   /redirect/external?url=javascript:alert(document.cookie)
        //   /redirect/external?url=http://evil.com/phishing
        //   /redirect/external?url=//evil.com
        try {
            response.sendRedirect(url);  // 未校验！
        } catch (IOException e) {
            throw new RuntimeException("重定向失败", e);
        }
    }

    /**
     * 短链接跳转 — 使用短码跳转到目标地址
     * 
     * @param code 短链接码
     * @param response HTTP 响应
     */
    @GetMapping("/short")
    public void redirectByShortCode(
            @RequestParam("code") String code,
            HttpServletResponse response) {

        // 根据短码查找目标 URL（实际项目会从数据库或其他存储中查找）
        String targetUrl = lookupUrl(code);

        // 如果找不到，直接将 code 作为 URL 跳转 —— 又一个漏洞点！
        if (targetUrl == null) {
            targetUrl = code;  // 用户输入直接作为跳转目标
        }

        try {
            response.sendRedirect(targetUrl);
        } catch (IOException e) {
            throw new RuntimeException("重定向失败", e);
        }
    }

    /**
     * 模拟短链接映射
     */
    private String lookupUrl(String code) {
        // 模拟数据库查询
        if ("home".equals(code)) return "/dashboard";
        if ("profile".equals(code)) return "/profile";
        return null;  // 找不到时返回 null
    }
}
