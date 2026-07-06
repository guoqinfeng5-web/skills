package com.xsslab.scenario02;

import org.springframework.stereotype.Service;

@Service("scenario02AuthService")
public class AuthService {

    public boolean authenticate(String username, String password) {
        return "admin".equals(username) && "admin123".equals(password);
    }

    public int getRemainingAttempts(String username) {
        return 3;
    }

    public void logFailedAttempt(String username, String ip) {
        // intentionally no escaping when logging
    }
}
