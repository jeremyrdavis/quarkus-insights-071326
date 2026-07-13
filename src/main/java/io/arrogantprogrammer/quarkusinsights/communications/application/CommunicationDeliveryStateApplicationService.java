package io.arrogantprogrammer.quarkusinsights.communications.application;

import io.arrogantprogrammer.quarkusinsights.communications.domain.aggregates.CommunicationDelivery;
import io.arrogantprogrammer.quarkusinsights.communications.domain.valueobjects.EmailMessage;
import io.arrogantprogrammer.quarkusinsights.communications.persistence.CommunicationDeliveryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Short, isolated delivery-state transactions. Each method runs in its own
 * {@code REQUIRES_NEW} transaction so the non-transactional batch orchestrator
 * never holds a database transaction open across an SMTP call. Must be invoked
 * from a separate bean (never self-invocation) for the interceptors to apply.
 */
@ApplicationScoped
public class CommunicationDeliveryStateApplicationService {

    private final CommunicationDeliveryRepository deliveryRepository;

    public CommunicationDeliveryStateApplicationService(CommunicationDeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<UUID> findDue(Instant now, Duration staleTimeout, int batchSize) {
        return deliveryRepository.findDueDeliveryIds(now, staleTimeout, batchSize);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Optional<EmailMessage> claim(UUID deliveryId, Instant now) {
        Optional<CommunicationDelivery> found = deliveryRepository.findDomainById(deliveryId);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        CommunicationDelivery delivery = found.get();
        EmailMessage message = delivery.claimForDelivery(now);
        deliveryRepository.update(delivery);
        return Optional.of(message);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void markDelivered(UUID deliveryId, Instant deliveredAt) {
        CommunicationDelivery delivery = deliveryRepository.findDomainById(deliveryId)
                .orElseThrow(() -> new IllegalStateException("CommunicationDelivery not found: " + deliveryId));
        delivery.markDelivered(deliveredAt);
        deliveryRepository.update(delivery);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void markFailed(UUID deliveryId, String error, Instant nextAttemptAt, int maxAttempts) {
        CommunicationDelivery delivery = deliveryRepository.findDomainById(deliveryId)
                .orElseThrow(() -> new IllegalStateException("CommunicationDelivery not found: " + deliveryId));
        delivery.scheduleRetry(error, nextAttemptAt, maxAttempts);
        deliveryRepository.update(delivery);
    }
}
