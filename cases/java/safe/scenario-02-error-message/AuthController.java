package com.xss.safe.scenario02;

import org.springframework.web.util.HtmlUtils;

/**
 * Safe version: error messages are HTML-escaped before being stored in the model.
 */
public class AuthController {

    public String login(String username, String password) {
        try {
            authenticate(username, password);
        } catch (Exception e) {
            // Safe: escape before adding to model
            String safeMsg = HtmlUtils.htmlEscape(e.getMessage());
            javax.servlet.http.HttpServletRequest request = null; // injected by framework
            request.setAttribute("errorMsg", safeMsg);
        }
        return "error";
    }

    private void authenticate(String user, String pass) {
        throw new RuntimeException("Invalid credentials for user: " + user);
    }
}
