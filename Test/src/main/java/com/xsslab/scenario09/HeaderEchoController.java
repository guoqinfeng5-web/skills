package com.xsslab.scenario09;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/scenario09/echo")
public class HeaderEchoController {

    @GetMapping
    public String echoPage(
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestHeader(value = "Referer", required = false) String referer,
            @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
            @CookieValue(value = "displayName", required = false) String displayName,
            @RequestParam(value = "msg", required = false) String msg,
            HttpServletRequest request,
            Model model) {

        model.addAttribute("userAgent", userAgent);
        model.addAttribute("referer", referer);
        model.addAttribute("forwardedFor", forwardedFor);
        model.addAttribute("displayName", displayName);
        model.addAttribute("msg", msg);
        model.addAttribute("requestUri", request.getRequestURI());
        model.addAttribute("queryString", request.getQueryString());
        return "scenario09/echo";
    }
}
