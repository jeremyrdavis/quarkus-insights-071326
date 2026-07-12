package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.SubmissionContext;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.CfpRepository;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.ConferenceSessionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SubmissionContextService {

    @Inject
    CfpRepository cfpRepository;

    @Inject
    ConferenceSessionRepository conferenceSessionRepository;

    public SubmissionContext getSubmissionContext(EmailAddress presenterEmail) {

//        return new SubmissionContext();
        return null;
    }
}
