package com.xsslab.scenario06;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/scenario06/auth")
public class LoginController {

    private final AuthService authService;

    public LoginController(@org.springframework.beans.factory.annotation.Qualifier("scenario06AuthService") AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "redirect", required = false) String redirectUrl,
            Model model) {
        model.addAttribute("redirect", redirectUrl);
        model.addAttribute("pageTitle", "用户登录");
        return "scenario06/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam(value = "redirect", required = false) String redirectUrl,
            HttpSession session) {

        boolean authenticated = authService.authenticate(username, password);

        if (authenticated) {
            session.setAttribute("currentUser", username);
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                return "redirect:" + redirectUrl;
            }
            return "redirect:/";
        }

        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            return "redirect:/scenario06/auth/login?error=1&redirect=" + redirectUrl;
        }
        return "redirect:/scenario06/auth/login?error=1";
    }
}
