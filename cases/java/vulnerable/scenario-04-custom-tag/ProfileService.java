package com.example.profile.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户个人资料服务
 */
@Service
public class ProfileService {

    /**
     * 模拟用户资料缓存
     */
    private final Map<String, String> userBioMap = new ConcurrentHashMap<>();
    private final Map<String, String> displayNameMap = new ConcurrentHashMap<>();

    public ProfileService() {
        // 模拟一些默认用户数据
        userBioMap.put("admin", "系统管理员，热爱编码和咖啡");
        displayNameMap.put("admin", "管理员");
    }

    /**
     * 获取用户简介（实际项目会从数据库查询）
     * 
     * @param username 用户名
     * @return 用户简介文字
     */
    public String getUserBio(String username) {
        // 从数据库或缓存获取
        return userBioMap.getOrDefault(username, 
            "这个用户很懒，还没有填写个人简介");
    }

    /**
     * 获取展示名称
     */
    public String getDisplayName(String username) {
        return displayNameMap.getOrDefault(username, username);
    }

    /**
     * 记录个人资料查看日志
     */
    public void logProfileView(String username) {
        // 实际项目写入日志表
        System.out.println("[PROFILE] View profile: " + username + 
                         " at " + System.currentTimeMillis());
    }

    /**
     * 更新用户简介（用户输入进入数据库，之后展示时同样可能触发 XSS）
     */
    public void updateUserBio(String username, String bio) {
        userBioMap.put(username, bio);
    }
}
