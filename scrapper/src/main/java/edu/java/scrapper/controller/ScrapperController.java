package edu.java.scrapper.controller;

import edu.java.models.dto.request.AddLinkRequest;
import edu.java.models.dto.request.RemoveLinkRequest;
import edu.java.models.dto.response.LinkResponse;
import edu.java.models.dto.response.ListLinksResponse;
import edu.java.scrapper.exception.AlreadyExistException;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.exception.RepeatedRegistrationException;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.TgChatService;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ScrapperController implements ScrapperApi {
    private final TgChatService tgChatService;
    private final LinkService linkService;

    // как избавиться от этого throws в сигнатуре? у меня же вроде есть @RestControllerAdvice
    @Override
    public void registerChat(@PathVariable Long id) throws RepeatedRegistrationException {
        log.info("Process request on /tg-chat/{} POST", id);
        tgChatService.register(id);
    }

    @Override
    public void deleteChat(@PathVariable Long id) throws NotExistException {
        log.info("Process request on /tg-chat/{} DELETE", id);
        tgChatService.unregister(id);
    }

    @Override
    public ListLinksResponse getLinks(@RequestHeader("Tg-Chat-Id") Long id) {
        log.info("Process request on /links GET for {}", id);
        List<LinkResponse> links = linkService.listAll(id)
            .stream()
            .map(l -> new LinkResponse(l.id(), URI.create(l.url())))
            .toList();
        return new ListLinksResponse(links, links.size());
    }

    @Override
    public void addLink(@RequestHeader("Tg-Chat-Id") Long id, AddLinkRequest addLinkRequest)
        throws RepeatedRegistrationException, AlreadyExistException {
        log.info("Process request on /links POST for {} and {}", id, addLinkRequest);
        linkService.add(id, addLinkRequest.link());
    }

    @Override
    public void deleteLink(@RequestHeader("Tg-Chat-Id") Long id, RemoveLinkRequest removeLinkRequest)
        throws NotExistException {
        log.info("Process request on /links DELETE for {} and {}", id, removeLinkRequest);
        linkService.remove(id, URI.create(removeLinkRequest.link()));
    }

}
