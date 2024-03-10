package edu.java.bot.service;

import edu.java.bot.model.Link;
import edu.java.bot.repository.LinkRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinkService {
    private final LinkRepository linkRepository;

    @Autowired
    public LinkService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    public Integer track(Long userId, String link) {
        linkRepository.addLink(userId, link);
        return 1; // TODO: здесь потом будет возвращаться автоматически сгенерированный id из crud.
    }

    public String list(Long userId) {
        List<Link> links = linkRepository.getUserLinks(userId);
        if (links != null) {
           return String.join("\n", links.stream().map(Link::url).toList());
        }
        return null;
    }

    public boolean untrack(Long userId, String link) {
        return linkRepository.deleteLink(userId, link);
    }
}
