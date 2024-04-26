package edu.java.bot.configuration.backoff_retry;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.exception.RetryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "app", name = "retry-config.back-off-type", havingValue = "const")
public class ConstBackoffConfiguration {
    @Bean
    public Retry backoffRetry(ApplicationConfig config) {
        return Retry.fixedDelay(config.retryConfig().attempts(), config.retryConfig().minDelay())
            .filter(throwable -> throwable instanceof WebClientResponseException && config.retryConfig().statusCodes()
                .contains(((WebClientResponseException) throwable).getStatusCode().value()))
            .doBeforeRetry(s -> log.info("Perform retry on {}", s.failure().getLocalizedMessage()))
            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                log.error("Max attempts reached");
                throw new RetryException("External Service failed to process after max attempts");
            })
            .jitter(config.retryConfig().jitter());
    }
}
