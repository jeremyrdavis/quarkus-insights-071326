package io.arrogantprogrammer.quarkusinsights.cfp.application.outbox;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.events.SessionProposalAcceptedEvent;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.outbox.CfpOutboxEventEntity;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.outbox.CfpOutboxEventRepository;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Publishes one outbox row per {@code REQUIRES_NEW} transaction. The typed CDI
 * event is fired SYNCHRONOUSLY: if a Communications observer throws, the
 * exception propagates and this transaction rolls back, leaving the row PENDING.
 * The row is marked PUBLISHED in the same transaction as the observer's insert,
 * giving the durable at-least-once handoff.
 */
@ApplicationScoped
public class CfpOutboxPublishApplicationService {

    private final CfpOutboxEventRepository outboxRepository;
    private final Event<SessionProposalAcceptedEvent> acceptedEvent;

    public CfpOutboxPublishApplicationService(CfpOutboxEventRepository outboxRepository,
                                              Event<SessionProposalAcceptedEvent> acceptedEvent) {
        this.outboxRepository = outboxRepository;
        this.acceptedEvent = acceptedEvent;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<UUID> findDueEventIds(Instant now, int batchSize) {
        return outboxRepository.findDueEventIds(now, batchSize);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void publish(UUID eventId) {
        CfpOutboxEventEntity entity = outboxRepository.findRequired(eventId);
        Instant now = Instant.now();
        if (!entity.isPublishableAt(now)) {
            Log.debugf("Outbox event %s no longer publishable (status=%s) — skipping",
                    eventId, entity.getStatus());
            return;
        }
        SessionProposalAcceptedEvent event = deserialize(entity);
        // Synchronous — a Communications failure must propagate and roll this back.
        acceptedEvent.fire(event);
        entity.markPublished(now);
        Log.infof("Outbox published event=%s type=%s proposal=%s attempt=%d result=PUBLISHED",
                entity.getEventId(), entity.getEventType(), entity.getAggregateId(), entity.getAttemptCount());
    }

    private SessionProposalAcceptedEvent deserialize(CfpOutboxEventEntity entity) {
        if (!SessionProposalAcceptedEvent.EVENT_TYPE.equals(entity.getEventType())
                || entity.getEventVersion() != SessionProposalAcceptedEvent.CURRENT_VERSION) {
            throw new IllegalStateException("Unsupported outbox event type/version: "
                    + entity.getEventType() + " / " + entity.getEventVersion());
        }
        return outboxRepository.deserializeAccepted(entity.getPayload());
    }
}
