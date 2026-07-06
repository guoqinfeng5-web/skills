package com.example.search.model;

import java.util.List;

/**
 * 业务包装层 —— 负责对搜索结果做统一封装
 * 
 * 团队引入此类是为了"统一格式化业务数据"，
 * 但封装过程中未考虑 XSS 防护，
 * 直接将用户输入放入了 HTML 片段中。
 */
public class BusinessWrapper {

    private String displayContent;
    private String rawQuery;
    private int resultCount;

    private BusinessWrapper() {}

    /**
     * 包装搜索结果 —— 注意：rawQuery 是用户原始输入，未做转义
     */
    public static BusinessWrapper wrap(List<String> products, String rawQuery) {
        BusinessWrapper wrapper = new BusinessWrapper();
        wrapper.rawQuery = rawQuery;
        wrapper.resultCount = products.size();

        // 构造用于前端展示的 HTML 内容
        // 这里拼接了包含用户输入的 HTML 片段 —— 漏洞点！
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"search-summary\">");
        sb.append("  搜索结果：共找到 ").append(products.size()).append(" 条记录");
        sb.append("</div>");
        sb.append("<div class=\"search-query\">");
        sb.append("  当前搜索词：").append(rawQuery);  // 未转义！
        sb.append("</div>");
        sb.append("<ul class=\"product-list\">");
        for (String product : products) {
            sb.append("  <li>").append(product).append("</li>");
        }
        sb.append("</ul>");

        wrapper.displayContent = sb.toString();
        return wrapper;
    }

    /**
     * 获取展示内容 — 这个值直接输出到 JSP，包含未转义的用户输入
     */
    public String getDisplayContent() {
        return displayContent;
    }

    public String getRawQuery() {
        return rawQuery;
    }

    public int getResultCount() {
        return resultCount;
    }
}
