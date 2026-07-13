package io.arrogantprogrammer.quarkusinsights.communications.infrastructure.events;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.events.SessionProposalAcceptedEvent;
import io.arrogantprogrammer.quarkusinsights.communications.application.CommunicationsApplicationService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

/**
 * Inbound adapter: observes the CFP published event synchronously and delegates
 * to the Communications application service. Intentionally thin — no email
 * composition, no Mailer, no CFP repository access, no transaction phase. It
 * runs inside the outbox publisher's transaction so a persistence failure
 * propagates and rolls the publication back.
 */
@ApplicationScoped
public class SessionProposalEventListener {

    private final CommunicationsApplicationService communications;

    public SessionProposalEventListener(CommunicationsApplicationService communications) {
        this.communications = communications;
    }

    void onSessionProposalAccepted(@Observes SessionProposalAcceptedEvent event) {
        communications.recordAcceptedProposal(event);
    }
}
