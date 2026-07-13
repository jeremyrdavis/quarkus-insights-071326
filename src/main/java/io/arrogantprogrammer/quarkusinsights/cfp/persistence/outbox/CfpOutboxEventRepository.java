package io.arrogantprogrammer.quarkusinsights.cfp.persistence.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.events.SessionProposalAcceptedEvent;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Repository for the CFP outbox. Mutations here intentionally carry no
 * {@code @Transactional} annotation: they must run inside the caller's
 * transaction (the acceptance transaction for {@link #append}, the publisher's
 * {@code REQUIRES_NEW} transaction for the state changes) so persistence stays
 * atomic with the surrounding work.
 */
@ApplicationScoped
public class CfpOutboxEventRepository implements PanacheRepository<CfpOutboxEventEntity> {

    @Inject
    ObjectMapper objectMapper;

    public void append(SessionProposalAcceptedEvent event) {
        String payload = serialize(event);
        CfpOutboxEventEntity entity = new CfpOutboxEventEntity(
                event.eventId(),
                SessionProposalAcceptedEvent.EVENT_TYPE,
                SessionProposalAcceptedEvent.CURRENT_VERSION,
                event.proposalId(),
                event.occurredAt(),
                payload,
                CfpOutboxStatus.PENDING,
                0,
                event.occurredAt());
        persist(entity);
    }

    /** Oldest-due-first event IDs that are still PENDING and past their next-attempt time. */
    public List<UUID> findDueEventIds(Instant now, int batchSize) {
        return find("status = ?1 and nextAttemptAt <= ?2",
                Sort.by("nextAttemptAt").ascending().and("occurredAt", Sort.Direction.Ascending),
                CfpOutboxStatus.PENDING, now)
                .page(Page.ofSize(batchSize))
                .list()
                .stream()
                .map(CfpOutboxEventEntity::getEventId)
                .toList();
    }

    public CfpOutboxEventEntity findRequired(UUID eventId) {
        CfpOutboxEventEntity entity = find("eventId", eventId).firstResult();
        if (entity == null) {
            throw new IllegalStateException("CFP outbox event not found: " + eventId);
        }
        return entity;
    }

    public void markPublished(UUID eventId, Instant publishedAt) {
        findRequired(eventId).markPublished(publishedAt);
    }

    public void recordFailure(UUID eventId, String error, Instant nextAttemptAt, int maxAttempts) {
        findRequired(eventId).recordFailure(error, nextAttemptAt, maxAttempts);
    }

    public String serialize(SessionProposalAcceptedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize outbox event " + event.eventId(), e);
        }
    }

    public SessionProposalAcceptedEvent deserializeAccepted(String payload) {
        try {
            return objectMapper.readValue(payload, SessionProposalAcceptedEvent.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize outbox payload", e);
        }
    }
}
