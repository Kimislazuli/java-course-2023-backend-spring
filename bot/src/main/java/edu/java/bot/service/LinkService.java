package edu.java.bot.service;

import edu.java.bot.client.ScrapperClient;
import edu.java.bot.model.Link;
import edu.java.bot.repository.LinkRepository;
import edu.java.models.dto.response.LinkResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinkService {
    private final LinkRepository linkRepository;
    private final ScrapperClient client;

    @Autowired
    public LinkService(LinkRepository linkRepository, ScrapperClient client) {
        this.linkRepository = linkRepository;
        this.client = client;
    }

    public Long track(Long userId, String link) {
        linkRepository.addLink(userId, link);
        Optional<LinkResponse> response = client.addLink(userId, link);
        if (response.isPresent()) {
            return response.get().id();
        }
        return -1L;
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
