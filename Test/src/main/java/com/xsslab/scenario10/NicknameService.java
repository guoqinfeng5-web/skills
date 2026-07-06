package com.xsslab.scenario10;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class NicknameService {

    private final CopyOnWriteArrayList<NicknameEntry> leaderboard = new CopyOnWriteArrayList<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public NicknameEntry register(String nickname, String motto, int score) {
        NicknameEntry entry = new NicknameEntry();
        entry.setId(idGen.getAndIncrement());
        entry.setNickname(nickname);
        entry.setMotto(motto);
        entry.setScore(score);
        entry.setRegisteredAt(LocalDateTime.now());
        entry.setFormattedTime(entry.getRegisteredAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        leaderboard.add(entry);
        return entry;
    }

    public List<NicknameEntry> getLeaderboard() {
        List<NicknameEntry> copy = new ArrayList<>(leaderboard);
        copy.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        return copy;
    }
}
