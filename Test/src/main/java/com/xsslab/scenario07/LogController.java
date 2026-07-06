package com.xsslab.scenario07;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/scenario07/admin/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping
    public String viewLogs(
            @RequestParam(value = "level", defaultValue = "ALL") String level,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model) {

        List<LogEntry> logs = logService.queryLogs(level, keyword, page);

        model.addAttribute("logs", logs);
        model.addAttribute("currentLevel", level);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("stats", logService.getStats(level));

        return "scenario07/admin";
    }

    @GetMapping("/detail/{logId}")
    public String viewLogDetail(
            @PathVariable("logId") Long logId,
            Model model) {

        LogEntry logEntry = logService.getLogById(logId);
        if (logEntry == null) {
            model.addAttribute("error", "日志不存在: " + logId);
            return "scenario07/admin";
        }

        model.addAttribute("logDetail", logEntry);
        model.addAttribute("pageTitle", "日志详情 - " + logEntry.getMessage());
        return "scenario07/admin";
    }

    @GetMapping("/search")
    @ResponseBody
    public List<LogEntry> searchLogs(@RequestParam("q") String query) {
        return logService.searchLogs(query);
    }

    @PostMapping("/record")
    @ResponseBody
    public String recordLog(@RequestParam("message") String message,
                            @RequestParam(value = "detail", required = false) String detail) {
        logService.recordUserInput(message, detail);
        return "ok";
    }
}
