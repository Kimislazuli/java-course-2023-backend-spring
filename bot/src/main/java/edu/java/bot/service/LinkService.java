package edu.java.bot.service;

import edu.java.bot.client.ScrapperClient;
import edu.java.models.dto.response.LinkResponse;
import edu.java.models.dto.response.ListLinksResponse;
import java.net.URI;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LinkService {
    private final ScrapperClient client;

    @Autowired
    public LinkService(ScrapperClient client) {
        this.client = client;
    }

    public Long track(Long userId, String link) {
        Optional<LinkResponse> response = client.addLink(userId, link);
        if (response.isPresent()) {
            return response.get().id();
        }
        return -1L;
    }

    public String list(Long userId) {
        ListLinksResponse linksResponse = client.getLinks(userId);
        return String.join("\n", linksResponse.links().stream().map(LinkResponse::url).map(URI::toString).toList());
    }

    public boolean untrack(Long userId, String link) {
        try {
            client.deleteLink(userId, link);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
