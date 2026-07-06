package com.xsslab.scenario08;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AnnouncementService {

    private final CopyOnWriteArrayList<Announcement> store = new CopyOnWriteArrayList<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public Announcement save(String title, String content, String author) {
        Announcement item = new Announcement();
        item.setId(idGen.getAndIncrement());
        item.setTitle(title);
        item.setContent(content);
        item.setAuthor(author != null ? author : "匿名");
        item.setCreatedAt(LocalDateTime.now());
        item.setFormattedTime(item.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        store.add(0, item);
        return item;
    }

    public List<Announcement> findAll() {
        return new ArrayList<>(store);
    }

    public Announcement findById(Long id) {
        return store.stream().filter(a -> a.getId().equals(id)).findFirst().orElse(null);
    }
}
