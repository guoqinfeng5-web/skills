package com.xsslab.scenario07;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class LogService {

    private final CopyOnWriteArrayList<LogEntry> logs = new CopyOnWriteArrayList<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public LogService() {
        seedSampleLogs();
    }

    public void recordUserInput(String message, String detail) {
        recordAction("USER_INPUT", "anonymous", message, detail);
    }

    public void recordAction(String action, String user, String message, String detail) {
        LogEntry entry = new LogEntry();
        entry.setId(idGen.getAndIncrement());
        entry.setLevel("INFO");
        entry.setAction(action);
        entry.setUser(user != null ? user : "anonymous");
        entry.setMessage(message);
        entry.setDetail(detail != null ? detail : message);
        entry.setIpAddress("127.0.0.1");
        entry.setUserAgent("TestAgent");
        entry.setTimestamp(LocalDateTime.now());
        entry.setFormattedTime(entry.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        logs.add(0, entry);
    }

    public List<LogEntry> queryLogs(String level, String keyword, int page) {
        List<LogEntry> filtered = logs.stream()
                .filter(log -> "ALL".equals(level) || level.equals(log.getLevel()))
                .filter(log -> keyword == null || keyword.isEmpty()
                        || log.getMessage().contains(keyword)
                        || log.getDetail().contains(keyword))
                .collect(Collectors.toList());

        int pageSize = 20;
        int from = (page - 1) * pageSize;
        if (from >= filtered.size()) {
            return new ArrayList<>();
        }
        return filtered.subList(from, Math.min(from + pageSize, filtered.size()));
    }

    public LogEntry getLogById(Long logId) {
        return logs.stream().filter(l -> l.getId().equals(logId)).findFirst().orElse(null);
    }

    public List<LogEntry> searchLogs(String query) {
        return logs.stream()
                .filter(log -> log.getMessage().contains(query) || log.getDetail().contains(query))
                .collect(Collectors.toList());
    }

    public LogStats getStats(String level) {
        LogStats stats = new LogStats();
        stats.setTotalCount(logs.size());
        stats.setInfoCount((int) logs.stream().filter(l -> "INFO".equals(l.getLevel())).count());
        stats.setWarnCount((int) logs.stream().filter(l -> "WARN".equals(l.getLevel())).count());
        stats.setErrorCount((int) logs.stream().filter(l -> "ERROR".equals(l.getLevel())).count());
        return stats;
    }

    private void seedSampleLogs() {
        recordUserInput("系统启动完成", "应用正常启动");
        recordUserInput("用户访问首页", "GET /");
    }

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
