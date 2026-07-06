package com.xsslab.scenario04;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/scenario04/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public String viewProfile(
            @RequestParam(value = "username", defaultValue = "guest") String username,
            @RequestParam(value = "level", required = false) String level,
            @RequestParam(value = "avatar", required = false) String avatar,
            Model model) {

        ProfileData profile = profileService.loadProfile(username);

        model.addAttribute("profileUser", username);
        model.addAttribute("userBio", profile.getBio());
        model.addAttribute("displayName", profile.getDisplayName());
        model.addAttribute("userLevel", level != null ? level : profile.getLevel());
        model.addAttribute("userAvatar", avatar != null ? avatar : profile.getAvatar());

        return "scenario04/profile";
    }

    @GetMapping("/{username}")
    public String viewProfileByPath(
            @PathVariable("username") String username,
            Model model) {

        ProfileData profile = profileService.loadProfile(username);
        model.addAttribute("profileUser", username);
        model.addAttribute("userBio", profile.getBio());
        model.addAttribute("displayName", profile.getDisplayName());
        model.addAttribute("userLevel", profile.getLevel());
        model.addAttribute("userAvatar", profile.getAvatar());
        return "scenario04/profile";
    }
}
