package com.xsslab.scenario01;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/scenario01/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ModelAndView search(
            @RequestParam("q") String query,
            @RequestParam(value = "page", defaultValue = "1") int page) {

        SearchResult result = searchService.search(query, page);

        ModelAndView mav = new ModelAndView("scenario01/search");
        mav.addObject("result", result);
        mav.addObject("page", page);
        mav.addObject("query", query);
        return mav;
    }

    @GetMapping("/suggest")
    public ModelAndView suggest(@RequestParam("q") String query) {
        SearchResult result = searchService.suggest(query);
        ModelAndView mav = new ModelAndView("scenario01/search");
        mav.addObject("result", result);
        mav.addObject("suggestion", query);
        return mav;
    }
}
