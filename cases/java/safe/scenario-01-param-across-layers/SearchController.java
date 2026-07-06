package com.xss.safe.scenario01;

/**
 * Safe version: Input is HTML-escaped before being passed across layers.
 */
public class SearchController {

    private final SearchService searchService = new SearchService();

    public String search(String rawQuery) {
        // Safe: HTML-escape at controller boundary before passing to service
        String safeQuery = org.springframework.web.util.HtmlUtils.htmlEscape(rawQuery);
        SearchResult result = searchService.processQuery(safeQuery);
        return "search";
    }
}
