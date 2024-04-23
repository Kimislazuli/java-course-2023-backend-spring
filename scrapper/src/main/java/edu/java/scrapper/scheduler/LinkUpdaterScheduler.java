package edu.java.scrapper.scheduler;

import edu.java.models.dto.LinkUpdate;
import edu.java.models.dto.UpdateType;
import edu.java.scrapper.client.GithubClient;
import edu.java.scrapper.client.StackOverflowClient;
import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.dto.github.GithubActivityResponse;
import edu.java.scrapper.dto.github.GithubResponse;
import edu.java.scrapper.dto.stackoverflow.StackOverflowResponse;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.service.processing_services.LinkService;
import edu.java.scrapper.service.processing_services.UpdaterService;
import edu.java.scrapper.service.sending_services.SenderService;
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
    private final SenderService service;
    private final UpdaterService updaterService;
    private final LinkService linkService;
    private final GithubClient githubClient;
    private final StackOverflowClient stackOverflowClient;

    private static final String PUSH = "push";
    private static final String PR = "pr_merge";

    @Scheduled(fixedDelayString = "${app.scheduler.interval}")
    public void update() throws NotExistException {
        log.info("Check updates");
        List<Link> links = updaterService.findOldLinksToUpdate(OffsetDateTime.now().minus(Duration.ofMinutes(10)));
        for (Link link : links) {
            updaterService.check(link.getId(), OffsetDateTime.now());
            if (link.getUrl().contains("github")) {
                githubUpdate(link);
            } else {
                stackOverflowUpdate(link);
            }
        }
        log.info("Check completed");
    }

    public void githubUpdate(Link link) throws NotExistException {
        String[] urlParts = link.getUrl().split("/");
        String owner = urlParts[urlParts.length - 2];
        String repo = urlParts[urlParts.length - 1];
        Optional<GithubResponse> optionalGithubResponse = githubClient.fetchLastModificationTime(owner, repo);
        if (optionalGithubResponse.isPresent()) {
            GithubResponse githubResponse = optionalGithubResponse.get();
            if (link.getLastUpdate().isBefore(githubResponse.lastModified())) {
                List<GithubActivityResponse> activityResponses =
                    githubClient.fetchActivity(owner, repo, githubResponse.lastModified());
                for (GithubActivityResponse response : activityResponses) {
                    if (response.activityType().equals(PUSH) || response.activityType().equals(PR)) {
                        performTableUpdateAndTelegramNotification(
                            link.getId(),
                            link.getUrl(),
                            githubResponse.lastModified(),
                            response.activityType(),
                            response.actor().login()
                        );
                    } else {
                        performTableUpdateAndTelegramNotification(
                            link.getId(),
                            link.getUrl(),
                            githubResponse.lastModified()
                        );
                    }
                }

            }
        }
    }

    public void stackOverflowUpdate(Link link) throws NotExistException {
        String[] urlParts = link.getUrl().split("/");
        int question = Integer.getInteger(urlParts[urlParts.length - 1]);
        StackOverflowResponse stackOverflowResponse = stackOverflowClient.fetchLastModificationTime(question);
        if (link.getLastUpdate().isBefore(stackOverflowResponse.lastModified())) {
            performTableUpdateAndTelegramNotification(
                link.getId(),
                link.getUrl(),
                stackOverflowResponse.lastModified()
            );
        }
    }

    public void performTableUpdateAndTelegramNotification(long linkId, String url, OffsetDateTime updatedAt)
        throws NotExistException {
        updaterService.update(linkId, updatedAt);
        List<Long> linkedChatIds = linkService.linkedChatIds(linkId);
        service.send(new LinkUpdate(linkId, url, "", linkedChatIds, UpdateType.DEFAULT));
    }

    public void performTableUpdateAndTelegramNotification(
        long linkId,
        String url,
        OffsetDateTime updatedAt,
        String activityType,
        String actorLogin
    )
        throws NotExistException {
        updaterService.update(linkId, updatedAt);
        List<Long> linkedChatIds = linkService.linkedChatIds(linkId);
        if (activityType.equals(PUSH)) {
            service.send(new LinkUpdate(linkId, url, actorLogin, linkedChatIds, UpdateType.PUSH));
        } else if (activityType.equals(PR)) {
            service.send(new LinkUpdate(linkId, url, actorLogin, linkedChatIds, UpdateType.PR_MERGE));
        } else {
            service.send(new LinkUpdate(linkId, url, "", linkedChatIds, UpdateType.DEFAULT));
        }
    }
}
