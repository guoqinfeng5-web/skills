package com.example.log.service;

import com.example.log.model.LogEntry;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 日志服务 — 记录和查询系统操作日志
 * 
 * 注意: 记录日志时没有对用户输入做转义，
 * 日志中的用户输入直接存储，查询时直接返回
 */
@Service
public class LogService {

    private final AtomicLong idGenerator = new AtomicLong(1);
    private final List<LogEntry> logStore = new CopyOnWriteArrayList<>();

    /**
     * 记录操作日志
     * 
     * @param action  操作类型
     * @param user    操作用户
     * @param message 日志消息（可能包含用户输入的内容）
     * @param detail  详细内容（可能包含用户输入的内容）
     * @param level   日志级别
     * @return 记录的日志条目
     */
    public LogEntry recordLog(String action, String user, String message, 
                                String detail, String level) {
        LogEntry entry = new LogEntry();
        entry.setId(idGenerator.getAndIncrement());
        entry.setAction(action);
        entry.setUser(user);
        entry.setMessage(message);      // 用户输入直接存！未转义！
        entry.setDetail(detail);         // 用户输入直接存！未转义！
        entry.setLevel(level != null ? level : "INFO");
        entry.setTimestamp(new Date());
        entry.setIpAddress("127.0.0.1");
        entry.setUserAgent("Mozilla/5.0");

        logStore.add(entry);
        return entry;
    }

    /**
     * 模拟一些日志数据 — 包含用户提交的内容
     */
    public void simulateLogData() {
        // 模拟用户评论日志
        recordLog("COMMENT", "user1", 
            "用户发表评论", 
            "评论内容: 这个商品真不错！推荐给大家！", 
            "INFO");

        // 模拟搜索日志（用户输入的搜索词直接记录）
        recordLog("SEARCH", "anonymous",
            "用户搜索了商品",
            "搜索词: 手机 充电器 耳机", 
            "INFO");

        // 模拟错误日志（包含用户输入的错误信息）
        recordLog("FEEDBACK", "user2",
            "用户提交反馈", 
            "反馈内容: 页面加载太慢了，请优化性能！",
            "INFO");
    }

    /**
     * 查询日志
     * 
     * @param level   日志级别（ALL 表示全部）
     * @param keyword 搜索关键词（在日志消息和详情中搜索）
     * @param page    页码
     * @return 匹配的日志列表
     */
    public List<LogEntry> queryLogs(String level, String keyword, int page) {
        return logStore.stream()
            .filter(log -> "ALL".equals(level) || level.equals(log.getLevel()))
            .filter(log -> keyword == null || keyword.isEmpty() 
                || log.getMessage().contains(keyword)
                || log.getDetail().contains(keyword))
            .skip((page - 1) * 20)
            .limit(20)
            .collect(Collectors.toList());
    }

    /**
     * 根据 ID 获取日志
     */
    public LogEntry getLogById(Long id) {
        return logStore.stream()
            .filter(log -> log.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    /**
     * 搜索日志（全文搜索）
     */
    public List<LogEntry> searchLogs(String query) {
        return logStore.stream()
            .filter(log -> log.getMessage().contains(query)
                || log.getDetail().contains(query)
                || log.getUser().contains(query))
            .collect(Collectors.toList());
    }

    /**
     * 获取日志统计
     */
    public LogStats getStats(String level) {
        LogStats stats = new LogStats();
        stats.setTotalCount(logStore.size());
        stats.setInfoCount((int) logStore.stream().filter(l -> "INFO".equals(l.getLevel())).count());
        stats.setWarnCount((int) logStore.stream().filter(l -> "WARN".equals(l.getLevel())).count());
        stats.setErrorCount((int) logStore.stream().filter(l -> "ERROR".equals(l.getLevel())).count());
        return stats;
    }

    /**
     * 日志统计封装
     */
    public static class LogStats {
        private int totalCount;
        private int infoCount;
        private int warnCount;
        private int errorCount;

        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        public int getInfoCount() { return infoCount; }
        public void setInfoCount(int infoCount) { this.infoCount = infoCount; }
        public int getWarnCount() { return warnCount; }
        public void setWarnCount(int warnCount) { this.warnCount = warnCount; }
        public int getErrorCount() { return errorCount; }
        public void setErrorCount(int errorCount) { this.errorCount = errorCount; }
    }
}
