package com.example.search.service;

import com.example.search.model.BusinessWrapper;
import com.example.search.model.SearchResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索核心服务
 */
@Service
public class SearchService {

    /**
     * 执行商品搜索
     * 
     * @param rawQuery 用户输入的原始搜索词
     * @param page     当前页码
     * @return 封装后的搜索结果
     */
    public SearchResult search(String rawQuery, int page) {
        // ---- 这里模拟真实的业务处理流程 ----

        // 1. 对查询词做分词处理（看起来像安全处理，但不是 HTML 转义）
        String[] tokens = rawQuery.trim().split("\\s+");

        // 2. 模拟从数据库查询商品 (省略实际DB调用)
        List<String> products = queryProducts(tokens);

        // 3. 生成搜索摘要信息 —— 这里将用户原始输入放入了摘要中！
        //    团队本意是展示"您搜索的是: xxx"，但没有对 xxx 做 HTML 转义
        String summary = "您搜索的是: " + rawQuery + "，共找到 " + products.size() + " 条结果";

        // 4. 通过 BusinessWrapper 包装数据
        BusinessWrapper wrapper = BusinessWrapper.wrap(products, rawQuery);

        // 5. 最终构造返回结果
        SearchResult result = new SearchResult();
        result.setProducts(products);
        result.setDisplayContent(wrapper.getDisplayContent());
        result.setSearchQuery(rawQuery);                // 未转义
        result.setSummary(summary);                     // 包含未转义的原始输入
        result.setSuggestion("你是不是想找: " + rawQuery); // 又一处未转义
        result.setPage(page);

        return result;
    }

    /**
     * 搜索建议 - 同样未对输入做转义
     */
    public SearchResult suggest(String query) {
        SearchResult result = new SearchResult();
        result.setSearchQuery(query);
        // 直接将原始查询作为"建议"内容返回
        result.setSuggestion("猜您想搜索: " + query);
        result.setDisplayContent("<div class='suggest-item'>" + query + "</div>");
        return result;
    }

    /**
     * 模拟商品查询
     */
    private List<String> queryProducts(String[] tokens) {
        List<String> products = new ArrayList<>();
        // 模拟数据库查询结果
        for (String token : tokens) {
            if (token.length() > 0) {
                products.add("【商品】相关结果 - " + token);
            }
        }
        return products;
    }
}
