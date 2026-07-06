package com.xsslab.scenario06;

import org.springframework.stereotype.Service;

@Service("scenario06AuthService")
public class AuthService {

    public boolean authenticate(String username, String password) {
        return "admin".equals(username) && "admin123".equals(password);
    }
}
