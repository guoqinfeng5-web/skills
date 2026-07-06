package com.xsslab.common;

import javax.servlet.http.HttpSession;

public final class UserSessionHelper {

    private static final String SESSION_USER = "currentUser";
    private static final String SESSION_LAST_SEARCH = "lastSearchQuery";
    private static final String SESSION_LAST_PAGE = "lastPage";

    private UserSessionHelper() {}

    public static String getCurrentUser(HttpSession session) {
        if (session == null) return "anonymous";
        Object user = session.getAttribute(SESSION_USER);
        return user != null ? user.toString() : "anonymous";
    }

    public static void setCurrentUser(HttpSession session, String username) {
        session.setAttribute(SESSION_USER, username);
    }

    public static void rememberSearch(HttpSession session, String query) {
        session.setAttribute(SESSION_LAST_SEARCH, query);
    }

    public static String getLastSearch(HttpSession session) {
        if (session == null) return "";
        Object q = session.getAttribute(SESSION_LAST_SEARCH);
        return q != null ? q.toString() : "";
    }

    public static void rememberLastPage(HttpSession session, String page) {
        session.setAttribute(SESSION_LAST_PAGE, page);
    }

    public static String getLastPage(HttpSession session) {
        if (session == null) return "/";
        Object p = session.getAttribute(SESSION_LAST_PAGE);
        return p != null ? p.toString() : "/";
    }
}
