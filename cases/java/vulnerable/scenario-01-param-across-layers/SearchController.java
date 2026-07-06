package com.example.search.controller;

import com.example.search.model.SearchResult;
import com.example.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * 搜索功能控制器
 */
@Controller
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 处理商品搜索请求
     * @param q 搜索关键词（用户输入）
     * @param page 页码
     * @return 搜索结果页面
     */
    @GetMapping
    public ModelAndView search(
            @RequestParam("q") String query,
            @RequestParam(value = "page", defaultValue = "1") int page) {

        // 调用搜索服务，传入用户输入的查询词
        // 注意：query 直接来源于用户输入，未做任何清洗
        SearchResult result = searchService.search(query, page);

        ModelAndView mav = new ModelAndView("search");
        mav.addObject("result", result);
        mav.addObject("page", page);
        mav.addObject("query", query);  // 未转义直接传入视图

        return mav;
    }

    /**
     * 搜索建议接口
     */
    @GetMapping("/suggest")
    public ModelAndView suggest(@RequestParam("q") String query) {
        SearchResult result = searchService.suggest(query);
        ModelAndView mav = new ModelAndView("search");
        // 未转义的查询词通过多个路径进入视图
        mav.addObject("result", result);
        mav.addObject("suggestion", query);
        return mav;
    }
}
