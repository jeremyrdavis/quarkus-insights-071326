package io.arrogantprogrammer.quarkusinsights.cfp.infrastructure.scheduling;

import io.arrogantprogrammer.quarkusinsights.cfp.application.outbox.CfpOutboxBatchApplicationService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Thin scheduler adapter draining the CFP outbox. Single-instance {@code SKIP}
 * concurrency; all JSON, CDI, and retry logic live in the application services.
 */
@ApplicationScoped
public class CfpOutboxScheduler {

    private final CfpOutboxBatchApplicationService batch;

    public CfpOutboxScheduler(CfpOutboxBatchApplicationService batch) {
        this.batch = batch;
    }

    @Scheduled(every = "${cfp.outbox.publisher.every:1s}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    void publishPendingEvents() {
        batch.publishPendingEvents();
    }
}
