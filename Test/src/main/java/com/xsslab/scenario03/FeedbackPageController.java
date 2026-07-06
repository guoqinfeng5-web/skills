package com.xsslab.scenario03;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/scenario03")
public class FeedbackPageController {

    @GetMapping("/feedback")
    public String feedbackPage() {
        return "scenario03/feedback";
    }
}
