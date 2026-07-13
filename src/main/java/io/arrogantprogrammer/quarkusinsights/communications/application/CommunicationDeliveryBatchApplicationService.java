package io.arrogantprogrammer.quarkusinsights.communications.application;

import io.arrogantprogrammer.quarkusinsights.communications.application.ports.EmailSender;
import io.arrogantprogrammer.quarkusinsights.communications.domain.valueobjects.EmailMessage;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Non-transactional orchestrator for the email-delivery batch. Claims and
 * records state through the separate {@link CommunicationDeliveryStateApplicationService}
 * (each call its own {@code REQUIRES_NEW} transaction) and calls the
 * {@link EmailSender} port outside of any transaction, so no DB transaction is
 * held open across SMTP.
 */
@ApplicationScoped
public class CommunicationDeliveryBatchApplicationService {

    private static final int MAX_ERROR_LENGTH = 500;

    private final CommunicationDeliveryStateApplicationService deliveryState;
    private final EmailSender emailSender;
    private final CommunicationsDeliveryConfig config;

    public CommunicationDeliveryBatchApplicationService(
            CommunicationDeliveryStateApplicationService deliveryState,
            EmailSender emailSender,
            CommunicationsDeliveryConfig config) {
        this.deliveryState = deliveryState;
        this.emailSender = emailSender;
        this.config = config;
    }

    public void processDueDeliveries() {
        List<UUID> due = deliveryState.findDue(Instant.now(), config.staleProcessingTimeout(), config.batchSize());
        for (UUID deliveryId : due) {
            processOne(deliveryId);
        }
    }

    private void processOne(UUID deliveryId) {
        Optional<EmailMessage> claimed;
        try {
            claimed = deliveryState.claim(deliveryId, Instant.now());
        } catch (RuntimeException e) {
            Log.warnf("Communications delivery %s could not be claimed: %s", deliveryId, sanitize(e));
            return;
        }
        if (claimed.isEmpty()) {
            return;
        }

        EmailMessage message = claimed.get();
        try {
            emailSender.send(message);
            deliveryState.markDelivered(deliveryId, Instant.now());
            Log.infof("Communications delivery %s channel=EMAIL delivered", deliveryId);
        } catch (RuntimeException e) {
            Instant nextAttemptAt = Instant.now().plus(config.retryDelay());
            deliveryState.markFailed(deliveryId, sanitize(e), nextAttemptAt, config.maxAttempts());
            Log.warnf("Communications delivery %s channel=EMAIL failed, retry scheduled: %s",
                    deliveryId, sanitize(e));
        }
    }

    private static String sanitize(Throwable t) {
        String message = t.getMessage();
        String firstLine = message == null ? "" : message.split("\\R", 2)[0];
        String summary = t.getClass().getSimpleName() + ": " + firstLine;
        return summary.length() > MAX_ERROR_LENGTH ? summary.substring(0, MAX_ERROR_LENGTH) : summary;
    }
}
