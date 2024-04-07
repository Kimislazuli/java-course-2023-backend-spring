package edu.java.scrapper.service.sending_services;

import edu.java.models.dto.LinkUpdate;
import edu.java.scrapper.configuration.ApplicationConfig;
import org.springframework.kafka.core.KafkaTemplate;

public class QueueSenderService implements SenderService {
    private final ApplicationConfig config;
    private final KafkaTemplate<String, LinkUpdate> kafka;

    public QueueSenderService(ApplicationConfig config, KafkaTemplate<String, LinkUpdate> kafka) {
        this.config = config;
        this.kafka = kafka;
    }

    @Override
    public void send(LinkUpdate update) {
        kafka.send(config.topic().name(), update);
    }
}
