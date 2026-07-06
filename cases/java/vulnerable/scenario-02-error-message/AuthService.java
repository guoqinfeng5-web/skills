package com.example.auth.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 认证服务
 */
@Service
public class AuthService {

    /**
     * 模拟用户存储（实际项目中会从数据库查询）
     */
    private static final Map<String, String> USER_DB = new ConcurrentHashMap<>();
    static {
        USER_DB.put("admin", "admin123");
        USER_DB.put("user1", "pass123");
    }

    /**
     * 登录失败尝试计数
     */
    private final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();

    /**
     * 认证用户
     */
    public boolean authenticate(String username, String password) {
        // 用户名和密码不能为空
        if (username == null || password == null) {
            return false;
        }

        // 从数据库查询用户密码并比对
        String storedPassword = USER_DB.get(username);
        if (storedPassword != null && storedPassword.equals(password)) {
            // 登录成功，清除失败计数
            failedAttempts.remove(username);
            return true;
        }

        // 记录失败次数
        failedAttempts.merge(username, 1, Integer::sum);
        return false;
    }

    /**
     * 记录失败尝试
     */
    public void logFailedAttempt(String username, String remoteAddr) {
        // 实际项目会写入操作日志表
        System.out.println("[AUTH] Failed login for user: " + username + 
                         " from IP: " + remoteAddr);
    }

    /**
     * 获取剩余尝试次数
     */
    public int getRemainingAttempts(String username) {
        int attempts = failedAttempts.getOrDefault(username, 0);
        return Math.max(0, 5 - attempts);
    }
}
