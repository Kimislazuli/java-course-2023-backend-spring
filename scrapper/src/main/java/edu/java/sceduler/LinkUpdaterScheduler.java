package edu.java.sceduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LinkUpdaterScheduler {

    @Scheduled(fixedDelayString = "#{@schedulerFixedDelay}")
    public void update() {
        log.info("check updates");
    }
}
