package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.SubmissionContext;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.Cfp;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.SessionProposal;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.CfpRepository;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.SessionProposalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SubmissionProposalApplicationService {

    @Inject
    CfpRepository cfpRepository;

    @Inject
    SessionProposalRepository sessionProposalRepository;

    public SubmissionContext getSubmissionContext(UUID cfpId, UUID presenterId) {
        Cfp cfp = cfpRepository.findByUUID(cfpId)
                .orElseThrow(() -> new IllegalArgumentException("CFP not found: " + cfpId));
        List<SessionProposal> currentSessions = sessionProposalRepository.findSessionProposalsByPresenterId(presenterId);
        return new SubmissionContext(cfp.getCfpOpens(), cfp.getCfpCloses(), cfp.getConferenceSessionFormats(), cfp.getTracks(), currentSessions);
    }
}
