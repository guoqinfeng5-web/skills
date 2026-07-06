package com.xss.safe.scenario06;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

/**
 * Safe version: validates redirect against whitelist.
 */
@Controller
public class RedirectController {

    @GetMapping("/goto")
    public String redirect(@RequestParam String url, HttpServletRequest request) {
        // Safe: whitelist validation
        String safeUrl = validateUrl(url);
        return "redirect:" + safeUrl;
    }

    private String validateUrl(String url) {
        if (url == null || url.isBlank()) {
            return "/home";
        }
        // Only allow relative paths (no external domains)
        if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("//")) {
            return "/home";
        }
        // Reject paths containing ".." to prevent path traversal
        if (url.contains("..")) {
            return "/home";
        }
        return UriComponentsBuilder.fromPath(url).build().encode().toUriString();
    }
}
