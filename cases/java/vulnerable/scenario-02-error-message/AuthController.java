package com.example.auth.controller;

import com.example.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 登录认证控制器
 */
@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 显示登录页面
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * 处理登录请求
     */
    @PostMapping("/login")
    public String login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam(value = "redirect", required = false) String redirectUrl,
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session) {

        // 校验用户名和密码
        boolean authenticated = authService.authenticate(username, password);

        if (authenticated) {
            // 登录成功，记录会话
            session.setAttribute("currentUser", username);
            session.setAttribute("loginTime", System.currentTimeMillis());

            // 如果有重定向地址则跳转
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                return "redirect:" + redirectUrl;
            }
            return "redirect:/dashboard";
        }

        // ---- 登录失败处理（漏洞点） ----
        // 直接将用户输入的用户名拼接到错误消息中，未做任何转义
        String errorMsg = "用户 " + username + " 登录失败，请检查用户名和密码";
        request.setAttribute("errorMsg", errorMsg);

        // 额外传递一些用户相关的信息到页面
        request.setAttribute("loginName", username);
        request.setAttribute("failTime", System.currentTimeMillis());
        request.setAttribute("remainingAttempts", 
            authService.getRemainingAttempts(username));

        // 记录登录失败日志
        authService.logFailedAttempt(username, request.getRemoteAddr());

        return "forward:/WEB-INF/jsp/error.jsp";
    }

    /**
     * 退出登录
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
}
