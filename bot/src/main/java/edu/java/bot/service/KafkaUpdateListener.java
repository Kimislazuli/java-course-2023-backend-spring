package edu.java.bot.service;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.models.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaUpdateListener {
    private final BotService botService;
    private final ApplicationConfig config;
    private final KafkaTemplate<String, LinkUpdate> kafka;

    @KafkaListener(topics = "${app.topic.name}",
                   groupId = "group1",
                   containerFactory = "kafkaListenerContainerFactory",
                   concurrency = "5")
    public void listen(LinkUpdate update) {
        try {
            botService.sendUpdatesInfo(update);
            log.info("Successfully processed update {}", update);
        } catch (Exception e) {
            kafka.send(config.deadLetterTopic().name(), update);
            log.info("Update {} caused exception", update);
        }
    }
}
