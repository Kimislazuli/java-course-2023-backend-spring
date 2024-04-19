package edu.java.scrapper.configuration;

import edu.java.models.dto.backoff_retry.BackoffType;
import edu.java.scrapper.configuration.database.AccessType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotNull
    Scheduler scheduler,
    AccessType databaseAccessType,
    @NotNull
    RetryConfig retryConfig,
    @NotNull
    TimeRateConfig timeRateConfig
) {
    public record TimeRateConfig(Duration duration, long capacity, long tokens) {
    }

    public record RetryConfig(@NotNull BackoffType backoffType, int attempts, List<Integer> statusCodes, @NotNull
    Duration minDelay, @Max(1) float jitter) {
    }

    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }
}
