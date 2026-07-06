package com.example.auth.controller;

import com.example.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * 登录控制器 — 包含重定向功能
 * 
 * 登录成功后支持跳转到之前访问的页面
 */
@Controller
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private AuthService authService;

    /**
     * 显示登录页面 — 将 redirect 参数传入视图
     * 
     * @param redirectUrl 重定向目标 URL（用户输入）
     * @param model       视图模型
     * @return 登录页面
     */
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "redirect", required = false) String redirectUrl,
            Model model) {

        // 将重定向地址传入登录页面（用于表单提交时保留）
        model.addAttribute("redirect", redirectUrl);

        // 如果有错误信息也展示
        model.addAttribute("pageTitle", "用户登录");

        return "login";
    }

    /**
     * 处理登录请求 — 登录成功后重定向
     * 
     * @param username     用户名
     * @param password     密码
     * @param redirectUrl  重定向目标 URL（来自用户输入的参数）
     * @param session      HTTP 会话
     * @return 重定向到用户之前访问的页面，或默认 dashboard
     */
    @PostMapping("/login")
    public String login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam(value = "redirect", required = false) String redirectUrl,
            HttpSession session) {

        boolean authenticated = authService.authenticate(username, password);

        if (authenticated) {
            session.setAttribute("currentUser", username);

            // ---- 漏洞点: 直接拼接重定向 URL ----
            // redirectUrl 来自用户输入，未做任何 URL 编码和安全校验
            // 攻击者可以传入 javascript: 伪协议或包含恶意脚本的 URL
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                // 直接使用 redirect: 前缀拼接用户输入 —— Spring 会执行重定向
                // 如果 redirectUrl = "javascript:alert(1)"，浏览器可能执行
                return "redirect:" + redirectUrl;
            }

            // 默认跳转到 dashboard
            return "redirect:/dashboard";
        }

        // 登录失败，回到登录页并携带参数
        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            return "redirect:/auth/login?error=1&redirect=" + redirectUrl;
        }
        return "redirect:/auth/login?error=1";
    }

    /**
     * 另一个重定向入口 —— 从 session 中获取上次访问的页面
     */
    @GetMapping("/redirect/last-page")
    public String redirectToLastPage(HttpSession session) {
        String lastPage = (String) session.getAttribute("lastPage");
        if (lastPage != null && !lastPage.isEmpty()) {
            // 同样未校验 —— 如果 lastPage 被篡改，也会导致 XSS
            return "redirect:" + lastPage;
        }
        return "redirect:/dashboard";
    }
}
