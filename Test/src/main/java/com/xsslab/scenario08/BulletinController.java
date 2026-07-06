package com.xsslab.scenario08;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/scenario08/bulletin")
public class BulletinController {

    private final AnnouncementService announcementService;

    public BulletinController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping
    public String board(Model model) {
        model.addAttribute("announcements", announcementService.findAll());
        return "scenario08/board";
    }

    @GetMapping("/post")
    public String postForm() {
        return "scenario08/post";
    }

    @PostMapping("/post")
    public String submit(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "author", required = false) String author) {
        announcementService.save(title, content, author);
        return "redirect:/scenario08/bulletin";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("item", announcementService.findById(id));
        return "scenario08/detail";
    }
}
