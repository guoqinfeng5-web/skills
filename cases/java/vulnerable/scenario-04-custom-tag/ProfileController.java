package com.example.profile.controller;

import com.example.profile.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户个人资料控制器
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    /**
     * 查看用户个人资料 —— 通过请求参数指定用户名
     */
    @GetMapping
    public String viewProfile(
            @RequestParam("username") String username,
            @RequestParam(value = "tab", defaultValue = "info") String tab,
            Model model) {

        // 获取用户资料（实际项目会查数据库）
        // 注意: username 来自用户输入，未做任何安全处理
        model.addAttribute("profileUser", username);
        model.addAttribute("currentTab", tab);

        // 获取用户的扩展信息（也可能包含来自其他地方的用户输入）
        String bio = profileService.getUserBio(username);
        model.addAttribute("userBio", bio);
        model.addAttribute("userAvatar", "/avatars/default.png");

        // 记录访问日志
        profileService.logProfileView(username);

        return "profile";
    }

    /**
     * 通过路径变量查看用户资料 —— 同样有 XSS 风险
     */
    @GetMapping("/{username}")
    public String viewProfileByPath(
            @PathVariable("username") String username,
            Model model) {

        model.addAttribute("profileUser", username);
        model.addAttribute("currentTab", "info");

        String displayName = profileService.getDisplayName(username);
        model.addAttribute("displayName", displayName);
        model.addAttribute("userAvatar", "/avatars/default.png");

        return "profile";
    }
}
