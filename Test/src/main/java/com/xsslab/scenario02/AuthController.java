package com.xsslab.scenario02;

import com.xsslab.common.AuditTrailService;
import com.xsslab.common.UserSessionHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/scenario02/auth")
public class AuthController {

    private final AuthService authService;
    private final AuditTrailService auditTrail;

    public AuthController(
            @org.springframework.beans.factory.annotation.Qualifier("scenario02AuthService") AuthService authService,
            AuditTrailService auditTrail) {
        this.authService = authService;
        this.auditTrail = auditTrail;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "scenario02/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request,
            HttpSession session) {

        boolean authenticated = authService.authenticate(username, password);

        if (authenticated) {
            UserSessionHelper.setCurrentUser(session, username);
            auditTrail.trace("LOGIN_SUCCESS", session, "用户登录成功", username);
            return "redirect:/portal/dashboard";
        }

        String errorMsg = "用户 " + username + " 登录失败，请检查用户名和密码";
        request.setAttribute("errorMsg", errorMsg);
        request.setAttribute("loginName", username);
        request.setAttribute("remainingAttempts", authService.getRemainingAttempts(username));
        authService.logFailedAttempt(username, request.getRemoteAddr());
        auditTrail.trace("LOGIN_FAIL", session, errorMsg, username);

        return "forward:/WEB-INF/jsp/scenario02/error.jsp";
    }
}
