package io.arrogantprogrammer.quarkusinsights.communications.infrastructure.scheduling;

import io.arrogantprogrammer.quarkusinsights.communications.application.CommunicationDeliveryBatchApplicationService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Thin scheduler adapter driving the email-delivery batch. Single-instance
 * {@code SKIP} concurrency; all persistence and composition live in the
 * application service.
 */
@ApplicationScoped
public class CommunicationDeliveryScheduler {

    private final CommunicationDeliveryBatchApplicationService batch;

    public CommunicationDeliveryScheduler(CommunicationDeliveryBatchApplicationService batch) {
        this.batch = batch;
    }

    @Scheduled(every = "${communications.delivery.every:1s}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    void deliverPending() {
        batch.processDueDeliveries();
    }
}
