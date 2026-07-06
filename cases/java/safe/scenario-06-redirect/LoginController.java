package com.xss.safe.scenario06;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Safe version: validates redirect target against a whitelist,
 * then uses UriComponentsBuilder for safe URL encoding.
 */
@Controller
public class LoginController {

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam(required = false) String redirect,
                        HttpServletRequest request) {
        // Dummy auth check
        if ("admin".equals(username) && "secret".equals(password)) {
            // Safe: validate and encode redirect
            String safeRedirect = validateAndEncodeRedirect(redirect, request);
            return "redirect:" + safeRedirect;
        }
        request.setAttribute("error", "Invalid credentials");
        return "login";
    }

    private String validateAndEncodeRedirect(String redirect, HttpServletRequest request) {
        if (redirect == null || redirect.isBlank()) {
            return "/home";
        }
        // Whitelist: only allow relative paths or same-origin redirects
        List<String> allowed = List.of("/home", "/dashboard", "/profile");
        String path = redirect.contains("?") ? redirect.substring(0, redirect.indexOf('?')) : redirect;
        if (allowed.contains(path)) {
            return UriComponentsBuilder.fromPath(redirect)
                    .build()
                    .encode()
                    .toUriString();
        }
        return "/home";
    }
}
