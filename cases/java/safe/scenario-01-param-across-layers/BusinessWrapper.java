package com.xss.safe.scenario01;

/**
 * Safe version: preserves HTML-escaped content in the wrapper.
 */
public class BusinessWrapper {

    public SearchResult wrap(String safeContent) {
        SearchResult result = new SearchResult();
        result.setDisplayContent(safeContent);
        return result;
    }

    public static class SearchResult {
        private String displayContent;

        public String getDisplayContent() {
            return displayContent;
        }

        public void setDisplayContent(String displayContent) {
            this.displayContent = displayContent;
        }
    }
}
