package com.xsslab.scenario03;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/scenario03")
public class FeedbackBoardController {

    private final FeedbackService feedbackService;

    public FeedbackBoardController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/board")
    public String board(Model model) {
        List<FeedbackResponse> feedbacks = feedbackService.getLatestFeedbacks(1);
        model.addAttribute("feedbacks", feedbacks);
        return "scenario03/board";
    }
}
