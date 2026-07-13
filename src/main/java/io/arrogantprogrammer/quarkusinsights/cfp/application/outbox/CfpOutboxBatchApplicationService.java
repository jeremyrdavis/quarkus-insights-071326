package io.arrogantprogrammer.quarkusinsights.cfp.application.outbox;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Non-transactional orchestrator: loads due outbox event IDs and publishes each
 * in its own transaction, recording a failure (separate transaction) and
 * continuing the batch when a publication rolls back.
 */
@ApplicationScoped
public class CfpOutboxBatchApplicationService {

    private static final int MAX_ERROR_LENGTH = 500;

    private final CfpOutboxPublishApplicationService publishService;
    private final CfpOutboxFailureApplicationService failureService;
    private final CfpOutboxConfig config;

    public CfpOutboxBatchApplicationService(CfpOutboxPublishApplicationService publishService,
                                            CfpOutboxFailureApplicationService failureService,
                                            CfpOutboxConfig config) {
        this.publishService = publishService;
        this.failureService = failureService;
        this.config = config;
    }

    public void publishPendingEvents() {
        List<UUID> due = publishService.findDueEventIds(Instant.now(), config.batchSize());
        for (UUID eventId : due) {
            try {
                publishService.publish(eventId);
            } catch (RuntimeException e) {
                Instant nextAttemptAt = Instant.now().plus(config.retryDelay());
                failureService.recordFailure(eventId, sanitize(e), nextAttemptAt, config.maxAttempts());
                Log.warnf("Outbox event=%s result=FAILED, retry scheduled: %s", eventId, sanitize(e));
            }
        }
    }

    private static String sanitize(Throwable t) {
        String message = t.getMessage();
        String firstLine = message == null ? "" : message.split("\\R", 2)[0];
        String summary = t.getClass().getSimpleName() + ": " + firstLine;
        return summary.length() > MAX_ERROR_LENGTH ? summary.substring(0, MAX_ERROR_LENGTH) : summary;
    }
}
