package io.arrogantprogrammer.quarkusinsights.communications.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.events.SessionProposalAcceptedEvent;
import io.arrogantprogrammer.quarkusinsights.communications.domain.aggregates.Communication;
import io.arrogantprogrammer.quarkusinsights.communications.persistence.CommunicationRepository;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

/**
 * Communications' inbound use case: translate an accepted-proposal event into a
 * durable Communication record + pending email delivery. Idempotent by
 * {@code sourceEventId} (the event ID), backed by a unique DB constraint.
 *
 * <p>Uses default {@code @Transactional} so it JOINS the outbox publisher's
 * dispatcher transaction: the Communication insert and the CFP outbox
 * {@code PUBLISHED} update commit — or roll back — together.
 */
@ApplicationScoped
public class CommunicationsApplicationService {

    private final CommunicationRepository communicationRepository;

    public CommunicationsApplicationService(CommunicationRepository communicationRepository) {
        this.communicationRepository = communicationRepository;
    }

    @Transactional
    public void recordAcceptedProposal(SessionProposalAcceptedEvent event) {
        if (communicationRepository.existsBySourceEventId(event.eventId())) {
            Log.infof("Communications: source event %s already recorded (duplicate) — no-op", event.eventId());
            return;
        }
        Communication communication = Communication.forAcceptedSessionProposal(
                event.eventId(),
                event.presenterFirstName(),
                event.presenterEmail(),
                event.proposalTitle(),
                event.occurredAt());
        communicationRepository.persistNew(communication);
        Log.infof("Communications: created communication %s (created) for source event %s",
                communication.getId(), event.eventId());
    }
}
