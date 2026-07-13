package io.arrogantprogrammer.quarkusinsights.communications;

import io.arrogantprogrammer.quarkusinsights.cfp.application.ChangeSessionProposalStatusCommand;
import io.arrogantprogrammer.quarkusinsights.cfp.application.CfpApplicationService;
import io.arrogantprogrammer.quarkusinsights.cfp.application.CreatePresenterCommand;
import io.arrogantprogrammer.quarkusinsights.cfp.application.CreateSessionProposalCommand;
import io.arrogantprogrammer.quarkusinsights.cfp.application.SessionProposalDTO;
import io.arrogantprogrammer.quarkusinsights.cfp.application.outbox.CfpOutboxBatchApplicationService;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceSessionFormat;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceTrack;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.FormatCode;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.Language;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.Level;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.ProgrammingLanguage;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.SessionProposalStatus;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.events.SessionProposalAcceptedEvent;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.outbox.CfpOutboxEventEntity;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.outbox.CfpOutboxEventRepository;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.outbox.CfpOutboxStatus;
import io.arrogantprogrammer.quarkusinsights.communications.application.CommunicationDeliveryBatchApplicationService;
import io.arrogantprogrammer.quarkusinsights.communications.application.CommunicationsApplicationService;
import io.arrogantprogrammer.quarkusinsights.communications.domain.DeliveryStatus;
import io.arrogantprogrammer.quarkusinsights.communications.persistence.CommunicationDeliveryEntity;
import io.arrogantprogrammer.quarkusinsights.communications.persistence.CommunicationDeliveryRepository;
import io.arrogantprogrammer.quarkusinsights.communications.persistence.CommunicationRepository;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class AcceptanceCommunicationsFlowTest {

    // CFP 44444444 is open Jul–Sep 2026 so SessionProposal.create() accepts submissions.
    private static final UUID OPEN_CFP_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

    @Inject
    CfpApplicationService cfpService;
    @Inject
    CfpOutboxBatchApplicationService outboxBatch;
    @Inject
    CommunicationDeliveryBatchApplicationService deliveryBatch;
    @Inject
    CommunicationsApplicationService communicationsService;
    @Inject
    CfpOutboxEventRepository outboxRepository;
    @Inject
    CommunicationRepository communicationRepository;
    @Inject
    CommunicationDeliveryRepository deliveryRepository;
    @Inject
    MockMailbox mailbox;

    @BeforeEach
    void clearMailbox() {
        mailbox.clear();
    }

    private static <T> T inTx(Callable<T> work) {
        return QuarkusTransaction.requiringNew().call(work);
    }

    private UUID setupSubmittedProposal(String email) {
        cfpService.registerPresenter(new CreatePresenterCommand(new EmailAddress(email), "Jane", "Doe"));
        CreateSessionProposalCommand command = new CreateSessionProposalCommand(
                OPEN_CFP_ID,
                "Supersonic Quarkus",
                "An abstract",
                new ConferenceSessionFormat(FormatCode.TECHNICAL_SESSION, "Technical", "Desc", Duration.ofMinutes(50)),
                new ConferenceTrack("ARCHITECTURE", "Architecture", "Desc"),
                Level.INTERMEDIATE,
                Language.ENGLISH,
                new EmailAddress(email),
                "1. Intro 2. Demo",
                List.of(new ProgrammingLanguage("Java")),
                "Basic Java");
        SessionProposalDTO dto = cfpService.createSessionProposal(command);
        return dto.id();
    }

    private void accept(UUID proposalId) {
        cfpService.reviewSessionProposal(new ChangeSessionProposalStatusCommand(proposalId, SessionProposalStatus.ACCEPTED));
    }

    @Test
    void endToEndAcceptanceSendsExactlyOneEmail() {
        String email = "e2e-" + UUID.randomUUID() + "@example.com";
        UUID proposalId = setupSubmittedProposal(email);

        accept(proposalId);

        // Exactly one PENDING outbox row carrying the presenter snapshot.
        List<OutboxRowView> rows = inTx(() ->
                outboxRepository.find("aggregateId", proposalId).<CfpOutboxEventEntity>list()
                        .stream()
                        .map(e -> new OutboxRowView(e.getEventId(), e.getStatus(), e.getEventType(), e.getPayload()))
                        .toList());
        assertEquals(1, rows.size());
        OutboxRowView row = rows.get(0);
        assertEquals(CfpOutboxStatus.PENDING, row.status());
        assertEquals(SessionProposalAcceptedEvent.EVENT_TYPE, row.eventType());
        assertTrue(row.payload().contains(email), "payload should carry the presenter email snapshot");
        UUID eventId = row.eventId();

        // Publish the outbox → Communications record created + row PUBLISHED, atomically.
        outboxBatch.publishPendingEvents();
        assertEquals(CfpOutboxStatus.PUBLISHED, inTx(() -> outboxRepository.findRequired(eventId).getStatus()));
        assertEquals(1L, inTx(() -> communicationRepository.count("sourceEventId", eventId)));

        // Deliver → exactly one email in MockMailbox, delivery DELIVERED.
        deliveryBatch.processDueDeliveries();
        List<Mail> sent = mailbox.getMessagesSentTo(email);
        assertEquals(1, sent.size());
        assertEquals("Your session proposal was accepted", sent.get(0).getSubject());
        assertEquals(DeliveryStatus.DELIVERED, inTx(() ->
                deliveryRepository.find("destination", email).<CommunicationDeliveryEntity>firstResult().getStatus()));

        // Re-running the publisher is a no-op: the row is already PUBLISHED.
        outboxBatch.publishPendingEvents();
        deliveryBatch.processDueDeliveries();
        assertEquals(1L, inTx(() -> communicationRepository.count("sourceEventId", eventId)));
        assertEquals(1, mailbox.getMessagesSentTo(email).size());
    }

    @Test
    void decliningDoesNotCreateAnOutboxEvent() {
        String email = "decline-" + UUID.randomUUID() + "@example.com";
        UUID proposalId = setupSubmittedProposal(email);

        cfpService.reviewSessionProposal(new ChangeSessionProposalStatusCommand(proposalId, SessionProposalStatus.DECLINED));

        assertEquals(0L, inTx(() -> outboxRepository.count("aggregateId", proposalId)));
    }

    @Test
    void duplicateEventReceiptIsIdempotent() {
        String email = "idem-" + UUID.randomUUID() + "@example.com";
        SessionProposalAcceptedEvent event = new SessionProposalAcceptedEvent(
                UUID.randomUUID(), Instant.now(), 1, UUID.randomUUID(), UUID.randomUUID(),
                "Title", UUID.randomUUID(), "Jane", "Doe", email);

        communicationsService.recordAcceptedProposal(event);
        communicationsService.recordAcceptedProposal(event);

        assertEquals(1L, inTx(() -> communicationRepository.count("sourceEventId", event.eventId())));
        assertEquals(1L, inTx(() -> deliveryRepository.count("destination", email)));
    }

    /** A detached snapshot so entity getters aren't called after the read transaction closes. */
    private record OutboxRowView(UUID eventId, CfpOutboxStatus status, String eventType, String payload) {
    }
}
