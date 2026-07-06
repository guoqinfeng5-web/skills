package com.example.feedback.service;

import com.example.feedback.model.FeedbackResponse;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 反馈处理服务
 * 
 * 做了"业务安全处理"（敏感词过滤），但未做 XSS 转义
 */
@Service
public class FeedbackService {

    private final AtomicLong idGenerator = new AtomicLong(1);
    private final Map<Long, FeedbackResponse> feedbackStore = new ConcurrentHashMap<>();

    /**
     * 模拟敏感词列表（看起来做了安全处理，但实际不够）
     */
    private static final Set<String> SENSITIVE_WORDS = new HashSet<>(
        Arrays.asList("fuck", "shit", "asshole")
    );

    /**
     * 处理用户提交的反馈内容
     * 
     * @param content  用户输入的原始内容
     * @param username 用户昵称
     * @return 处理后的反馈响应（注意：处理不包括 HTML 转义）
     */
    public FeedbackResponse processFeedback(String content, String username) {
        // ---- 看似做了安全处理，实际不够 ----

        // 1. 过滤敏感词（只替换了脏话，不防 XSS）
        String cleanedContent = filterSensitiveWords(content);

        // 2. 将 \n 转换为 <br/>（为了让前端正确换行，但这引入了 HTML 注入风险）
        String htmlContent = cleanedContent.replaceAll("\n", "<br/>");

        // 3. 格式化用户名（添加 emoji 修饰 —— 看起来"美化"了但未转义）
        String displayUser = "💬 " + username;

        // 4. 时间格式化
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedTime = sdf.format(new Date());

        // 5. 构造返回对象
        FeedbackResponse response = new FeedbackResponse();
        response.setFeedbackId(idGenerator.getAndIncrement());
        response.setDisplayContent(htmlContent);  // 未转义！
        response.setDisplayUser(displayUser);     // 未转义！
        response.setFormattedTime(formattedTime);
        response.setRawContent(content);

        // 存储到内存（模拟数据库）
        feedbackStore.put(response.getFeedbackId(), response);

        return response;
    }

    /**
     * 获取最近的反馈列表
     */
    public List<FeedbackResponse> getLatestFeedbacks(int page) {
        List<FeedbackResponse> all = new ArrayList<>(feedbackStore.values());
        all.sort((a, b) -> Long.compare(b.getFeedbackId(), a.getFeedbackId()));

        int start = (page - 1) * 10;
        int end = Math.min(start + 10, all.size());
        if (start >= all.size()) {
            return Collections.emptyList();
        }
        return all.subList(start, end);
    }

    /**
     * "敏感词过滤" —— 只处理了脏话，不防 XSS 攻击
     */
    private String filterSensitiveWords(String content) {
        if (content == null) return "";
        String result = content;
        for (String word : SENSITIVE_WORDS) {
            result = result.replaceAll(
                "(?i)" + Pattern.quote(word), 
                "***"
            );
        }
        return result;
    }
}

