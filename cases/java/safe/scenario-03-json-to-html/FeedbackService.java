package com.xss.safe.scenario03;

/**
 * Safe version: passes through already-escaped content.
 */
public class FeedbackService {

    public String process(String safeInput) {
        // Business logic applied to safe input
        return "Processed: " + safeInput;
    }
}
