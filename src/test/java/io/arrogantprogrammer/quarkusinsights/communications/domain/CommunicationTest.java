package io.arrogantprogrammer.quarkusinsights.communications.domain;

import io.arrogantprogrammer.quarkusinsights.communications.domain.aggregates.Communication;
import io.arrogantprogrammer.quarkusinsights.communications.domain.aggregates.CommunicationDelivery;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommunicationTest {

    @Test
    void acceptedProposalPolicyCreatesOneEmailDeliveryWithExpectedMessage() {
        Instant createdAt = Instant.parse("2026-07-13T10:15:30Z");
        Communication communication = Communication.forAcceptedSessionProposal(
                UUID.randomUUID(), "Jane", "jane@example.com", "Supersonic Quarkus", createdAt);

        assertEquals(CommunicationType.SESSION_PROPOSAL_ACCEPTED, communication.getType());
        assertEquals(1, communication.getDeliveries().size());

        CommunicationDelivery delivery = communication.getDeliveries().get(0);
        assertEquals(CommunicationChannel.EMAIL, delivery.getChannel());
        assertEquals("jane@example.com", delivery.getDestination());
        assertEquals(DeliveryStatus.PENDING, delivery.getStatus());
        assertEquals(createdAt, delivery.getNextAttemptAt());
        assertEquals("Your session proposal was accepted", delivery.getSubject());
        assertEquals("Hi Jane,\n\n"
                + "Your session proposal \"Supersonic Quarkus\" has been accepted.\n\n"
                + "Thank you for submitting.", delivery.getBody());
        assertTrue(delivery.getBody().contains("Jane"));
    }
}
