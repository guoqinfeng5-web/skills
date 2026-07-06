package com.xsslab.scenario06;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/scenario06/redirect")
public class RedirectController {

    @GetMapping("/external")
    public void redirectToExternal(
            @RequestParam("url") String url,
            HttpServletResponse response) {
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            throw new RuntimeException("重定向失败", e);
        }
    }

    @GetMapping("/short")
    public void redirectByShortCode(
            @RequestParam("code") String code,
            HttpServletResponse response) {
        String targetUrl = lookupUrl(code);
        if (targetUrl == null) {
            targetUrl = code;
        }
        try {
            response.sendRedirect(targetUrl);
        } catch (IOException e) {
            throw new RuntimeException("重定向失败", e);
        }
    }

    private String lookupUrl(String code) {
        if ("home".equals(code)) return "/";
        if ("profile".equals(code)) return "/scenario04/profile";
        return null;
    }
}
