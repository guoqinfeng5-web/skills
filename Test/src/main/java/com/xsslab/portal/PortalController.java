package com.xsslab.portal;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/portal")
public class PortalController {

    private final PortalService portalService;

    public PortalController(PortalService portalService) {
        this.portalService = portalService;
    }

    /**
     * 统一门户 — JSP 聚合搜索摘要、用户资料、最近日志。
     * 数据流: session → SearchService → BusinessWrapper → JSP EL
     *        session → ProfileService → 自定义 Tag
     *        AuditTrail → LogService → recentLogs EL
     */
    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(value = "q", required = false) String keyword,
            HttpSession session,
            Model model) {

        Map<String, Object> data = portalService.buildDashboardModel(session, keyword);
        model.addAllAttributes(data);
        return "portal/dashboard";
    }

    @GetMapping(value = "/api/search-snippet", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String searchSnippet(
            @RequestParam("q") String query,
            HttpSession session) {
        return portalService.buildSearchSnippet(query, session);
    }
}
