package com.xsslab.scenario03;

import com.xsslab.common.AuditTrailService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class FeedbackService {

    private final List<FeedbackResponse> store = new CopyOnWriteArrayList<>();
    private long idCounter = 1;
    private final AuditTrailService auditTrail;

    public FeedbackService(AuditTrailService auditTrail) {
        this.auditTrail = auditTrail;
    }

    public FeedbackResponse processFeedback(String content, String username) {
        FeedbackResponse response = new FeedbackResponse();
        response.setFeedbackId(String.valueOf(idCounter++));
        response.setDisplayUser(username != null ? username : "匿名");
        response.setDisplayContent(content);
        response.setFormattedTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        store.add(0, response);

        auditTrail.trace("FEEDBACK", username, "提交反馈: " + content, content);

        return response;
    }

    public List<FeedbackResponse> getLatestFeedbacks(int page) {
        int pageSize = 10;
        int from = (page - 1) * pageSize;
        if (from >= store.size()) {
            return new ArrayList<>();
        }
        int to = Math.min(from + pageSize, store.size());
        return new ArrayList<>(store.subList(from, to));
    }
}
