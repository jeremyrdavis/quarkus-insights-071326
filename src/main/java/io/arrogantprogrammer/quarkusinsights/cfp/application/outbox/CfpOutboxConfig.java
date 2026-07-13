package io.arrogantprogrammer.quarkusinsights.cfp.application.outbox;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

import java.time.Duration;

/**
 * CFP outbox publisher tuning. Bound from {@code cfp.outbox.publisher.*} so
 * batch size and retry policy are never hard-coded in the publisher.
 */
@ConfigMapping(prefix = "cfp.outbox.publisher")
public interface CfpOutboxConfig {

    /** Scheduler cadence. Consumed by the {@code @Scheduled} expression; mapped here so the prefix validates. */
    @WithDefault("1s")
    String every();

    @WithDefault("20")
    int batchSize();

    @WithDefault("5")
    int maxAttempts();

    @WithDefault("5s")
    Duration retryDelay();
}
