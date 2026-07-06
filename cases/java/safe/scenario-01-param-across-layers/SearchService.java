package com.xss.safe.scenario01;

/**
 * Safe version: Receives already-escaped data and passes it through unchanged.
 */
public class SearchService {

    public SearchResult processQuery(String safeQuery) {
        BusinessWrapper wrapper = new BusinessWrapper();
        return wrapper.wrap(safeQuery);
    }
}
