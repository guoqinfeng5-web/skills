package com.xsslab.scenario04;

import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    public ProfileData loadProfile(String username) {
        ProfileData data = new ProfileData();
        data.setDisplayName(username);
        data.setBio("这是 " + username + " 的个人简介");
        data.setLevel("Lv.5");
        data.setAvatar("/images/default-avatar.png");
        return data;
    }
}
