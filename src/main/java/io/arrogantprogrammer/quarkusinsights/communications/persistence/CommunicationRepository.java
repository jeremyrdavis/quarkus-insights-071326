package io.arrogantprogrammer.quarkusinsights.communications.persistence;

import io.arrogantprogrammer.quarkusinsights.communications.domain.aggregates.Communication;
import io.arrogantprogrammer.quarkusinsights.communications.domain.aggregates.CommunicationDelivery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

/**
 * Owns Communication ↔ entity mapping. Mutations run inside the caller's
 * transaction (the dispatcher transaction for {@link #persistNew}), so there is
 * no {@code @Transactional} here.
 */
@ApplicationScoped
public class CommunicationRepository implements PanacheRepository<CommunicationEntity> {

    public boolean existsBySourceEventId(UUID sourceEventId) {
        return count("sourceEventId", sourceEventId) > 0;
    }

    /** Persist a new Communication and each of its deliveries. */
    public void persistNew(Communication communication) {
        persist(toEntity(communication));
        for (CommunicationDelivery delivery : communication.getDeliveries()) {
            getEntityManager().persist(toDeliveryEntity(communication.getId(), delivery));
        }
    }

    private CommunicationEntity toEntity(Communication communication) {
        return new CommunicationEntity(
                communication.getId(),
                communication.getSourceEventId(),
                communication.getType(),
                communication.getCreatedAt());
    }

    private CommunicationDeliveryEntity toDeliveryEntity(UUID communicationId, CommunicationDelivery delivery) {
        return new CommunicationDeliveryEntity(
                delivery.getId(),
                communicationId,
                delivery.getChannel(),
                delivery.getDestination(),
                delivery.getSubject(),
                delivery.getBody(),
                delivery.getStatus(),
                delivery.getAttemptCount(),
                delivery.getNextAttemptAt(),
                delivery.getProcessingStartedAt(),
                delivery.getDeliveredAt(),
                delivery.getLastError());
    }
}
