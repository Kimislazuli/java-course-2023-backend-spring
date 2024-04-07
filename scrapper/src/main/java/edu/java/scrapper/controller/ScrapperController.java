package edu.java.scrapper.controller;

import edu.java.models.dto.request.AddLinkRequest;
import edu.java.models.dto.request.RemoveLinkRequest;
import edu.java.models.dto.response.ChatResponse;
import edu.java.models.dto.response.LinkResponse;
import edu.java.models.dto.response.ListLinksResponse;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.domain.model.chat.Chat;
import edu.java.scrapper.exception.AlreadyExistException;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.exception.RepeatedRegistrationException;
import edu.java.scrapper.service.processing_services.LinkService;
import edu.java.scrapper.service.processing_services.TgChatService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ScrapperController implements ScrapperApi {
    private final TgChatService tgChatService;
    private final LinkService linkService;
    private final Bucket bucket;

    @Autowired
    public ScrapperController(TgChatService tgChatService, LinkService linkService, ApplicationConfig config) {
        this.tgChatService = tgChatService;
        this.linkService = linkService;
        bucket = Bucket.builder()
            .addLimit(
                Bandwidth
                    .classic(
                        config.timeRateConfig().capacity(),
                        Refill.intervally(config.timeRateConfig().tokens(), config.timeRateConfig().duration())
                    )
            ).build();
    }

    @Override
    public ResponseEntity<Void> registerChat(@PathVariable Long id) throws RepeatedRegistrationException {
        if (bucket.tryConsume(1)) {
            log.info("Process request on /tg-chat/{} POST", id);
            tgChatService.register(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @Override
    public ResponseEntity<Void> deleteChat(@PathVariable Long id) throws NotExistException {
        if (bucket.tryConsume(1)) {
            log.info("Process request on /tg-chat/{} DELETE", id);
            tgChatService.unregister(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @Override
    public ResponseEntity<ChatResponse> getChat(Long id) {
        if (bucket.tryConsume(1)) {
            Optional<Chat> chat = tgChatService.getChat(id);
            return chat.map(
                value -> ResponseEntity.ok().body(new ChatResponse(value.getId(), value.getState()))
            ).orElseGet(
                () -> ResponseEntity.ok().body(new ChatResponse(-1, -1))
            );
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @Override
    public ResponseEntity<Void> changeChatState(@PathVariable Long id, @RequestHeader("State") int state)
        throws NotExistException {
        if (bucket.tryConsume(1)) {
            log.info("Process request on /tg-chat/{}/change_state POST", id);
            tgChatService.setState(id, state);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @Override
    public ResponseEntity<ListLinksResponse> getLinks(@RequestHeader("Tg-Chat-Id") Long id) {
        if (bucket.tryConsume(1)) {
            log.info("Process request on /links GET for {}", id);
            List<LinkResponse> links = linkService.listAll(id)
                .stream()
                .map(l -> new LinkResponse(l.getId(), URI.create(l.getUrl())))
                .toList();
            return ResponseEntity.ok().body(new ListLinksResponse(links, links.size()));
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @Override
    public ResponseEntity<Void> addLink(
        @RequestHeader("Tg-Chat-Id") Long id,
        @RequestBody AddLinkRequest addLinkRequest
    )
        throws RepeatedRegistrationException, AlreadyExistException, NotExistException {
        if (bucket.tryConsume(1)) {
            log.info("Process request on /links POST for {} and {}", id, addLinkRequest);
            linkService.add(id, addLinkRequest.link());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @Override
    public ResponseEntity<Void> deleteLink(
        @RequestHeader("Tg-Chat-Id") Long id,
        @RequestBody RemoveLinkRequest removeLinkRequest
    )
        throws NotExistException {
        if (bucket.tryConsume(1)) {
            log.info("Process request on /links DELETE for {} and {}", id, removeLinkRequest);
            linkService.remove(id, URI.create(removeLinkRequest.link()));
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

}
