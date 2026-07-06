package com.xsslab.portal;

import com.xsslab.common.UserSessionHelper;
import com.xsslab.scenario01.SearchResult;
import com.xsslab.scenario01.SearchService;
import com.xsslab.scenario04.ProfileData;
import com.xsslab.scenario04.ProfileService;
import com.xsslab.scenario07.LogEntry;
import com.xsslab.scenario07.LogService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 门户聚合服务 — 串联搜索、用户资料、日志等多个模块的数据。
 */
@Service
public class PortalService {

    private final SearchService searchService;
    private final ProfileService profileService;
    private final LogService logService;

    public PortalService(SearchService searchService,
                         ProfileService profileService,
                         LogService logService) {
        this.searchService = searchService;
        this.profileService = profileService;
        this.logService = logService;
    }

    public Map<String, Object> buildDashboardModel(HttpSession session, String keyword) {
        Map<String, Object> model = new HashMap<>();

        String username = UserSessionHelper.getCurrentUser(session);
        ProfileData profile = profileService.loadProfile(username);

        String effectiveQuery = keyword != null && !keyword.isEmpty()
                ? keyword
                : UserSessionHelper.getLastSearch(session);

        SearchResult searchResult = null;
        if (effectiveQuery != null && !effectiveQuery.isEmpty()) {
            searchResult = searchService.search(effectiveQuery, 1);
        }

        List<LogEntry> recentLogs = logService.queryLogs("ALL", null, 1);

        model.put("username", username);
        model.put("displayName", profile.getDisplayName());
        model.put("userBio", profile.getBio());
        model.put("userLevel", profile.getLevel());
        model.put("searchResult", searchResult);
        model.put("lastSearch", effectiveQuery);
        model.put("recentLogs", recentLogs);
        model.put("welcomeHtml", buildWelcomeBanner(username, effectiveQuery));

        return model;
    }

    /**
     * 供 AJAX 调用的 HTML 片段 — 拼接多个模块的未转义用户输入。
     */
    public String buildSearchSnippet(String query, HttpSession session) {
        SearchResult result = searchService.search(query, 1);
        UserSessionHelper.rememberSearch(session, query);

        String username = UserSessionHelper.getCurrentUser(session);
        StringBuilder html = new StringBuilder();
        html.append("<div class=\"portal-search-result\">");
        html.append("  <p>用户 <strong>").append(username).append("</strong> 搜索了：</p>");
        html.append("  <div class=\"search-body\">").append(result.getDisplayContent()).append("</div>");
        html.append("  <p class=\"suggestion\">").append(result.getSuggestion()).append("</p>");
        html.append("</div>");
        return html.toString();
    }

    private String buildWelcomeBanner(String username, String lastSearch) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"welcome-banner\">");
        sb.append("  <h2>欢迎回来，").append(username).append("</h2>");
        if (lastSearch != null && !lastSearch.isEmpty()) {
            sb.append("  <p>上次搜索：<em>").append(lastSearch).append("</em></p>");
        }
        sb.append("</div>");
        return sb.toString();
    }
}
