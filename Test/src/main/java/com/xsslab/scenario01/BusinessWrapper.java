package com.xsslab.scenario01;

import java.util.List;

public class BusinessWrapper {

    private String displayContent;
    private String rawQuery;
    private int resultCount;

    public static BusinessWrapper wrap(List<String> products, String rawQuery) {
        BusinessWrapper wrapper = new BusinessWrapper();
        wrapper.rawQuery = rawQuery;
        wrapper.resultCount = products.size();

        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"search-summary\">");
        sb.append("  搜索结果：共找到 ").append(products.size()).append(" 条记录");
        sb.append("</div>");
        sb.append("<div class=\"search-query\">");
        sb.append("  当前搜索词：").append(rawQuery);
        sb.append("</div>");
        sb.append("<ul class=\"product-list\">");
        for (String product : products) {
            sb.append("  <li>").append(product).append("</li>");
        }
        sb.append("</ul>");

        wrapper.displayContent = sb.toString();
        return wrapper;
    }

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
