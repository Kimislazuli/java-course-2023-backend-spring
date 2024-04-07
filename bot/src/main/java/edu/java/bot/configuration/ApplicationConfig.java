package edu.java.bot.configuration;

import edu.java.bot.configuration.backoff_retry.BackoffType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotEmpty
    String telegramToken,
    @NotNull
    RetryConfig retryConfig,
    @NotNull
    TimeRateConfig timeRateConfig,
    @NotNull
    TopicConfig topic,
    @NotNull
    TopicConfig deadLetterTopic
) {
    public record RetryConfig(@NotNull BackoffType backoffType, int attempts, List<Integer> statusCodes, @NotNull
    Duration minDelay, @Max(1) float jitter) {
    }

    public record TimeRateConfig(Duration duration, long capacity, long tokens) {
    }

    public record TopicConfig(String name, int partitions, int replicas) {
    }
}
