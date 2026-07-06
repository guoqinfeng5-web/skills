package com.example.log.controller;

import com.example.log.model.LogEntry;
import com.example.log.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 日志查看控制器 — 管理后台查看各类操作日志
 */
@Controller
@RequestMapping("/admin/logs")
public class LogController {

    @Autowired
    private LogService logService;

    /**
     * 查看日志列表 — 按级别过滤
     * 
     * @param level   日志级别（用户输入）
     * @param keyword 搜索关键词（用户输入）
     * @param model   视图模型
     * @return 日志管理页面
     */
    @GetMapping
    public String viewLogs(
            @RequestParam(value = "level", defaultValue = "ALL") String level,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model) {

        // 查询日志
        List<LogEntry> logs = logService.queryLogs(level, keyword, page);

        // ---- 漏洞点: 未转义的日志数据直接传入视图 ----
        model.addAttribute("logs", logs);                    // 日志内容可能含用户输入
        model.addAttribute("currentLevel", level);
        model.addAttribute("keyword", keyword);              // 搜索关键词也未经转义
        model.addAttribute("page", page);

        // 统计信息
        LogService.LogStats stats = logService.getStats(level);
        model.addAttribute("stats", stats);

        return "admin";
    }

    /**
     * 查看单条日志详情
     * 
     * @param logId 日志 ID
     * @param model 视图模型
     * @return 日志详情页面
     */
    @GetMapping("/detail/{logId}")
    public String viewLogDetail(
            @PathVariable("logId") Long logId,
            Model model) {

        LogEntry logEntry = logService.getLogById(logId);

        if (logEntry == null) {
            model.addAttribute("error", "日志不存在: " + logId);
            return "admin";
        }

        // ---- 漏洞点: 日志详情也未经转义 ----
        model.addAttribute("logDetail", logEntry);           // 日志详情包含用户输入
        model.addAttribute("pageTitle", "日志详情 - " + logEntry.getMessage());

        return "admin";
    }

    /**
     * 搜索日志 — 通过 AJAX 返回 JSON 格式
     */
    @GetMapping("/search")
    @ResponseBody
    public List<LogEntry> searchLogs(
            @RequestParam("q") String query) {

        // 直接返回日志条目（包含未转义的用户输入）
        // 前端如果直接 innerHTML 也会触发 XSS
        return logService.searchLogs(query);
    }
}
