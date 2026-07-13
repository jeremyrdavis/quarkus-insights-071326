package io.arrogantprogrammer.quarkusinsights.cfp.domain.events;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SessionProposalAcceptedEventTest {

    private static SessionProposalAcceptedEvent valid() {
        return new SessionProposalAcceptedEvent(
                UUID.randomUUID(), Instant.parse("2026-07-13T10:15:30Z"), 1,
                UUID.randomUUID(), UUID.randomUUID(), "Title",
                UUID.randomUUID(), "Jane", "Doe", "jane@example.com");
    }

    @Test
    void eventTypeIsStableWireIdentifier() {
        assertEquals("cfp.session-proposal.accepted", SessionProposalAcceptedEvent.EVENT_TYPE);
        assertEquals(1, SessionProposalAcceptedEvent.CURRENT_VERSION);
    }

    @Test
    void requiredFieldsRejectNull() {
        assertThrows(NullPointerException.class, () -> new SessionProposalAcceptedEvent(
                null, Instant.now(), 1, UUID.randomUUID(), UUID.randomUUID(), "T",
                UUID.randomUUID(), "Jane", "Doe", "jane@example.com"));
        assertThrows(NullPointerException.class, () -> new SessionProposalAcceptedEvent(
                UUID.randomUUID(), Instant.now(), 1, UUID.randomUUID(), UUID.randomUUID(), "T",
                null, "Jane", "Doe", "jane@example.com"));
    }

    @Test
    void stringFieldsRejectBlank() {
        assertThrows(IllegalArgumentException.class, () -> new SessionProposalAcceptedEvent(
                UUID.randomUUID(), Instant.now(), 1, UUID.randomUUID(), UUID.randomUUID(), "  ",
                UUID.randomUUID(), "Jane", "Doe", "jane@example.com"));
        assertThrows(IllegalArgumentException.class, () -> new SessionProposalAcceptedEvent(
                UUID.randomUUID(), Instant.now(), 1, UUID.randomUUID(), UUID.randomUUID(), "T",
                UUID.randomUUID(), "Jane", "Doe", ""));
    }

    @Test
    void eventVersionMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> new SessionProposalAcceptedEvent(
                UUID.randomUUID(), Instant.now(), 0, UUID.randomUUID(), UUID.randomUUID(), "T",
                UUID.randomUUID(), "Jane", "Doe", "jane@example.com"));
    }

    @Test
    void jacksonRoundTripPreservesAllFields() throws Exception {
        JsonMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
        SessionProposalAcceptedEvent original = valid();
        String json = mapper.writeValueAsString(original);
        SessionProposalAcceptedEvent restored = mapper.readValue(json, SessionProposalAcceptedEvent.class);
        assertEquals(original, restored);
    }
}
