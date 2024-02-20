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
        return links.get(userId);
    }

    public boolean deleteLink(Long userId, String url) {
        if (links.containsKey(userId)) {
            List<String> linksListForUser = links.get(userId).stream().map(Link::getUrl).toList();
            if (linksListForUser.contains(url)) {
                links.put(userId, links.get(userId).stream().filter(l -> !l.getUrl().equals(url)).toList());
                return true;
            }
        }
        return false;
    }
}
