package edu.java.scrapper.configuration.sender;

import edu.java.models.dto.LinkUpdate;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.service.sending_services.QueueSenderService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "true")
public class QueueSenderConfiguration {
    @Bean
    public QueueSenderService queueSender(ApplicationConfig config, KafkaTemplate<String, LinkUpdate> kafka) {
        return new QueueSenderService(config, kafka);
    }
}
