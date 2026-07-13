package io.arrogantprogrammer.quarkusinsights.communications;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.events.SessionProposalAcceptedEvent;
import io.arrogantprogrammer.quarkusinsights.communications.application.CommunicationDeliveryBatchApplicationService;
import io.arrogantprogrammer.quarkusinsights.communications.application.CommunicationsApplicationService;
import io.arrogantprogrammer.quarkusinsights.communications.application.ports.EmailSender;
import io.arrogantprogrammer.quarkusinsights.communications.domain.DeliveryStatus;
import io.arrogantprogrammer.quarkusinsights.communications.persistence.CommunicationDeliveryEntity;
import io.arrogantprogrammer.quarkusinsights.communications.persistence.CommunicationDeliveryRepository;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.QuarkusTestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
@TestProfile(EmailDeliveryRetryTest.ImmediateRetryProfile.class)
class EmailDeliveryRetryTest {

    /** Zero retry delay makes a scheduled retry immediately due, so recovery is deterministic without sleeping. */
    public static class ImmediateRetryProfile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("communications.delivery.retry-delay", "0s");
        }
    }

    @InjectMock
    EmailSender emailSender;
    @Inject
    CommunicationsApplicationService communicationsService;
    @Inject
    CommunicationDeliveryBatchApplicationService deliveryBatch;
    @Inject
    CommunicationDeliveryRepository deliveryRepository;

    private static <T> T inTx(Callable<T> work) {
        return QuarkusTransaction.requiringNew().call(work);
    }

    private DeliveryStatus statusFor(String email) {
        return inTx(() -> deliveryRepository.find("destination", email)
                .<CommunicationDeliveryEntity>firstResult().getStatus());
    }

    private int attemptsFor(String email) {
        return inTx(() -> deliveryRepository.find("destination", email)
                .<CommunicationDeliveryEntity>firstResult().getAttemptCount());
    }

    @Test
    void failedSendSchedulesRetryThenLaterSucceeds() {
        String email = "retry-" + UUID.randomUUID() + "@example.com";
        communicationsService.recordAcceptedProposal(new SessionProposalAcceptedEvent(
                UUID.randomUUID(), Instant.now(), 1, UUID.randomUUID(), UUID.randomUUID(),
                "Title", UUID.randomUUID(), "Jane", "Doe", email));

        // First attempt: SMTP fails → RETRY_SCHEDULED, attempt incremented.
        Mockito.doThrow(new RuntimeException("SMTP down")).when(emailSender).send(any());
        deliveryBatch.processDueDeliveries();
        assertEquals(DeliveryStatus.RETRY_SCHEDULED, statusFor(email));
        assertEquals(1, attemptsFor(email));

        // Second attempt: SMTP succeeds → DELIVERED. CFP state is never touched by delivery.
        Mockito.doNothing().when(emailSender).send(any());
        deliveryBatch.processDueDeliveries();
        assertEquals(DeliveryStatus.DELIVERED, statusFor(email));
    }
}
