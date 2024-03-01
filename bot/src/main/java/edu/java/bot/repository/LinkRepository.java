package edu.java.bot.repository;

import edu.java.bot.model.Link;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;

@Repository
public class LinkRepository {
    private final ConcurrentMap<Long, List<Link>> links = new ConcurrentHashMap<>();

    public void addLink(Long userId, String url) {
        links.putIfAbsent(userId, Collections.synchronizedList(new ArrayList<>()));
        List<Link> userLinks = links.get(userId);
        userLinks.add(new Link(url));
    }

    public List<Link> getUserLinks(Long userId) {
        List<Link> linkList = links.get(userId);
        if (linkList != null) {
            return Collections.unmodifiableList(linkList);
        }
        return null;
    }

    public boolean deleteLink(Long userId, String url) {
        List<Link> linkList = links.get(userId);
        if (linkList != null) {
            if (linkList.stream().anyMatch(l -> l.url().equals(url))) {
                linkList.removeIf(l -> l.url().equals(url));
                return true;
            }
        }
        return false;
    }
}
