package edu.java.bot.configuration.backoff_retry;

import edu.java.bot.configuration.ApplicationConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.util.retry.Retry;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "app", name = "retry-config.back-off-type", havingValue = "linear")
public class LinearBackoffConfiguration {
    @Bean
    public Retry backoffRetry(ApplicationConfig config) {
        return new LinearRetry(
            config.retryConfig().attempts(),
            config.retryConfig().minDelay(),
            config.retryConfig().statusCodes()
        );
    }
}
