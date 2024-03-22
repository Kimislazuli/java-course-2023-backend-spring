package edu.java.scrapper.sceduler;

import edu.java.scrapper.client.BotClient;
import edu.java.scrapper.client.GithubClient;
import edu.java.scrapper.client.StackOverflowClient;
import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.dto.github.GithubResponse;
import edu.java.scrapper.dto.stackoverflow.StackOverflowResponse;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.UpdaterService;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("MagicNumber")
public class LinkUpdaterScheduler {
    private final BotClient botClient;
    private final UpdaterService updaterService;
    private final LinkService linkService;
    private final GithubClient githubClient;
    private final StackOverflowClient stackOverflowClient;

    @Scheduled(fixedDelayString = "${app.scheduler.interval}")
    public void update() {
        log.info("Check updates");
        List<Link> links = updaterService.findOldLinksToUpdate(OffsetDateTime.now().minus(Duration.ofSeconds(10)));
        for (Link link : links) {
            updaterService.check(link.id(), OffsetDateTime.now());
            if (link.url().contains("github")) {
                githubUpdate(link);
            } else {
                stackOverflowUpdate(link);
            }
        }
        log.info("Check completed");
    }

    public void githubUpdate(Link link) {
        String[] urlParts = link.url().split("/");
        String owner = urlParts[urlParts.length - 2];
        String repo = urlParts[urlParts.length - 1];
        Optional<GithubResponse> optionalGithubResponse = githubClient.fetchLastModificationTime(owner, repo);
        if (optionalGithubResponse.isPresent()) {
            GithubResponse githubResponse = optionalGithubResponse.get();
            if (link.lastUpdate().isBefore(githubResponse.lastModified())) {
                performTableUpdateAndTelegramNotification(link.id(), link.url(), githubResponse.lastModified());
            }
        }
    }

    public void stackOverflowUpdate(Link link) {
        String[] urlParts = link.url().split("/");
        int question = Integer.getInteger(urlParts[urlParts.length - 1]);
        StackOverflowResponse stackOverflowResponse = stackOverflowClient.fetchLastModificationTime(question);
        if (link.lastUpdate().isBefore(stackOverflowResponse.lastModified())) {
            performTableUpdateAndTelegramNotification(link.id(), link.url(), stackOverflowResponse.lastModified());
        }
    }

    public void performTableUpdateAndTelegramNotification(long linkId, String url, OffsetDateTime updatedAt) {
        updaterService.update(linkId, updatedAt);
        List<Long> linkedChats = linkService.linkedChats(linkId);
        botClient.updates(linkId, url, "link updated", linkedChats);
    }
}
