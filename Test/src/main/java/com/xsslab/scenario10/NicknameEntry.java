package com.xsslab.scenario10;

import java.time.LocalDateTime;

public class NicknameEntry {

    private Long id;
    private String nickname;
    private String motto;
    private int score;
    private LocalDateTime registeredAt;
    private String formattedTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getMotto() { return motto; }
    public void setMotto(String motto) { this.motto = motto; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }
    public String getFormattedTime() { return formattedTime; }
    public void setFormattedTime(String formattedTime) { this.formattedTime = formattedTime; }
}
