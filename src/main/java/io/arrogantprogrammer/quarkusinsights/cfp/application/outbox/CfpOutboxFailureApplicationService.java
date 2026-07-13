package io.arrogantprogrammer.quarkusinsights.cfp.application.outbox;

import io.arrogantprogrammer.quarkusinsights.cfp.persistence.outbox.CfpOutboxEventRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Records outbox publication failures in their own {@code REQUIRES_NEW}
 * transaction, because the failing publish transaction has already rolled back.
 */
@ApplicationScoped
public class CfpOutboxFailureApplicationService {

    private final CfpOutboxEventRepository outboxRepository;

    public CfpOutboxFailureApplicationService(CfpOutboxEventRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void recordFailure(UUID eventId, String error, Instant nextAttemptAt, int maxAttempts) {
        outboxRepository.recordFailure(eventId, error, nextAttemptAt, maxAttempts);
    }
}
