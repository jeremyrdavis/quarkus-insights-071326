package io.arrogantprogrammer.quarkusinsights.communications.persistence;

import io.arrogantprogrammer.quarkusinsights.communications.domain.DeliveryStatus;
import io.arrogantprogrammer.quarkusinsights.communications.domain.aggregates.CommunicationDelivery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Owns CommunicationDelivery ↔ entity mapping and the due-delivery query.
 * State mutations run inside the caller's {@code REQUIRES_NEW} transaction.
 */
@ApplicationScoped
public class CommunicationDeliveryRepository implements PanacheRepository<CommunicationDeliveryEntity> {

    /**
     * Delivery IDs eligible for a send attempt, oldest-due first: PENDING or
     * RETRY_SCHEDULED past their next-attempt time, plus PROCESSING deliveries
     * whose processing started before the stale-processing cutoff (crash
     * recovery — at-least-once, so a reclaim may re-send).
     */
    public List<UUID> findDueDeliveryIds(Instant now, Duration staleTimeout, int batchSize) {
        Instant staleCutoff = now.minus(staleTimeout);
        return find("(status in (?1, ?2) and nextAttemptAt <= ?3) "
                        + "or (status = ?4 and processingStartedAt < ?5)",
                Sort.by("nextAttemptAt").ascending(),
                DeliveryStatus.PENDING, DeliveryStatus.RETRY_SCHEDULED, now,
                DeliveryStatus.PROCESSING, staleCutoff)
                .page(Page.ofSize(batchSize))
                .list()
                .stream()
                .map(CommunicationDeliveryEntity::getId)
                .toList();
    }

    public Optional<CommunicationDelivery> findDomainById(UUID id) {
        return find("id", id).<CommunicationDeliveryEntity>firstResultOptional().map(this::toDomain);
    }

    /** Write a mutated delivery's state back onto its managed entity. */
    public void update(CommunicationDelivery delivery) {
        CommunicationDeliveryEntity entity = find("id", delivery.getId()).firstResult();
        if (entity == null) {
            throw new IllegalStateException("CommunicationDelivery not found: " + delivery.getId());
        }
        entity.setStatus(delivery.getStatus());
        entity.setAttemptCount(delivery.getAttemptCount());
        entity.setNextAttemptAt(delivery.getNextAttemptAt());
        entity.setProcessingStartedAt(delivery.getProcessingStartedAt());
        entity.setDeliveredAt(delivery.getDeliveredAt());
        entity.setLastError(delivery.getLastError());
    }

    private CommunicationDelivery toDomain(CommunicationDeliveryEntity e) {
        return new CommunicationDelivery(
                e.getId(),
                e.getChannel(),
                e.getDestination(),
                e.getSubject(),
                e.getBody(),
                e.getStatus(),
                e.getAttemptCount(),
                e.getNextAttemptAt(),
                e.getProcessingStartedAt(),
                e.getDeliveredAt(),
                e.getLastError());
    }
}
