package com.xss.safe.scenario07;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Safe version: escapes log content before passing to admin view.
 */
@Controller
public class LogController {

    private final LogService logService = new LogService();

    @GetMapping("/admin/logs")
    public String viewLogs(@RequestParam(required = false) String filter, HttpServletRequest request) {
        String rawLogs = logService.getLogs(filter);
        // Safe: HTML-escape the full log output before sending to JSP
        String safeLogs = HtmlUtils.htmlEscape(rawLogs);
        request.setAttribute("logContent", safeLogs);
        return "admin";
    }
}
