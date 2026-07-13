package io.arrogantprogrammer.quarkusinsights.communications.application;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

import java.time.Duration;

/**
 * Communications delivery tuning. Bound from {@code communications.delivery.*}
 * so timing, batch size, and retry policy are never hard-coded in the workers.
 */
@ConfigMapping(prefix = "communications.delivery")
public interface CommunicationsDeliveryConfig {

    /** Scheduler cadence. Consumed by the {@code @Scheduled} expression; mapped here so the prefix validates. */
    @WithDefault("1s")
    String every();

    @WithDefault("20")
    int batchSize();

    @WithDefault("5")
    int maxAttempts();

    @WithDefault("5s")
    Duration retryDelay();

    @WithDefault("5m")
    Duration staleProcessingTimeout();
}
