package io.arrogantprogrammer.quarkusinsights.cfp.domain;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.Cfp;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.ConferenceSession;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.CfpEntity;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.CfpRepository;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.ConferenceSessionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SubmissionProposalService {

    @Inject
    CfpRepository cfpRepository;

    @Inject
    ConferenceSessionRepository conferenceSessionRepository;

    public SubmissionContext getSubmissionContext(UUID cfpId, UUID presenterId) {
        Cfp cfp = cfpRepository.findByUUID(cfpId)
                .orElseThrow(() -> new IllegalArgumentException("CFP not found: " + cfpId));
        List<ConferenceSession> currentSessions = conferenceSessionRepository.findSessionProposalsByPresenterId(presenterId);
        return new SubmissionContext(cfp.getCfpOpens(), cfp.getCfpCloses(), cfp.getConferenceSessionFormats(), cfp.getTracks(), currentSessions);
    }
}
