package com.xsslab.scenario10;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/scenario10/nickname")
public class NicknameController {

    private final NicknameService nicknameService;

    public NicknameController(NicknameService nicknameService) {
        this.nicknameService = nicknameService;
    }

    @GetMapping("/leaderboard")
    public String leaderboard(Model model) {
        model.addAttribute("entries", nicknameService.getLeaderboard());
        return "scenario10/leaderboard";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "scenario10/register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam("nickname") String nickname,
            @RequestParam(value = "motto", required = false) String motto,
            @RequestParam(value = "score", defaultValue = "0") int score) {
        nicknameService.register(nickname, motto, score);
        return "redirect:/scenario10/nickname/leaderboard";
    }
}
