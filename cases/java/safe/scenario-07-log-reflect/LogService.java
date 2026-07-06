package com.xss.safe.scenario07;

import java.util.List;

/**
 * Safe version: returns raw log data; escaping is done in the controller.
 */
public class LogService {

    public String getLogs(String filter) {
        // Simulated log retrieval
        List<String> entries = List.of(
            "User login: admin from 192.168.1.1",
            "File uploaded: report.pdf",
            "Error: NullPointerException in module X"
        );
        if (filter != null && !filter.isBlank()) {
            return String.join("\n", entries.stream()
                .filter(e -> e.contains(filter))
                .toList());
        }
        return String.join("\n", entries);
    }
}
