package edu.java.scrapper.configuration.sender;

import edu.java.scrapper.client.BotClient;
import edu.java.scrapper.service.sending_services.HttpSenderService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "false")
public class HttpSenderConfiguration {
    @Bean
    public HttpSenderService httpSender(BotClient client) {
        return new HttpSenderService(client);
    }
}
