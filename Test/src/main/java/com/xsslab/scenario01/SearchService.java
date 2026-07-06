package com.xsslab.scenario01;

import com.xsslab.common.AuditTrailService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {

    private final AuditTrailService auditTrail;

    public SearchService(AuditTrailService auditTrail) {
        this.auditTrail = auditTrail;
    }

    public SearchResult search(String rawQuery, int page) {
        String[] tokens = rawQuery.trim().split("\\s+");
        List<String> products = queryProducts(tokens);
        String summary = "您搜索的是: " + rawQuery + "，共找到 " + products.size() + " 条结果";

        BusinessWrapper wrapper = BusinessWrapper.wrap(products, rawQuery);

        SearchResult result = new SearchResult();
        result.setProducts(products);
        result.setDisplayContent(wrapper.getDisplayContent());
        result.setSearchQuery(rawQuery);
        result.setSummary(summary);
        result.setSuggestion("你是不是想找: " + rawQuery);
        result.setPage(page);

        auditTrail.trace("SEARCH", "anonymous", "用户搜索: " + rawQuery, rawQuery);

        return result;
    }

    public SearchResult suggest(String query) {
        SearchResult result = new SearchResult();
        result.setSearchQuery(query);
        result.setSuggestion("猜您想搜索: " + query);
        result.setDisplayContent("<div class='suggest-item'>" + query + "</div>");
        return result;
    }

    private List<String> queryProducts(String[] tokens) {
        List<String> products = new ArrayList<>();
        for (String token : tokens) {
            if (!token.isEmpty()) {
                products.add("【商品】相关结果 - " + token);
            }
        }
        return products;
    }
}
