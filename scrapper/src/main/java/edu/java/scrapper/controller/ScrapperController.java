package edu.java.scrapper.controller;

import edu.java.models.dto.request.AddLinkRequest;
import edu.java.models.dto.request.RemoveLinkRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ScrapperController implements ScrapperApi {
    @Override
    public ResponseEntity<Void> registerChat(@PathVariable Long id) {
        log.info("Process request on /tg-chat/{} POST", id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteChat(@PathVariable Long id) {
        log.info("Process request on /tg-chat/{} DELETE", id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> getLinks(@RequestHeader("Tg-Chat-Id") Long id) {
        log.info("Process request on /links GET for {}", id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> addLink(@RequestHeader("Tg-Chat-Id") Long id, AddLinkRequest addLinkRequest) {
        log.info("Process request on /links POST for {} and {}", id, addLinkRequest);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteLink(@RequestHeader("Tg-Chat-Id") Long id, RemoveLinkRequest removeLinkRequest) {
        log.info("Process request on /links DELETE for {} and {}", id, removeLinkRequest);
        return ResponseEntity.ok().build();
    }

}
