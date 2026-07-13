package io.arrogantprogrammer.quarkusinsights.cfp.infrastructure;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.SessionProposalStatus;
import jakarta.validation.constraints.NotNull;

public class ReviewSessionProposalParameters {

    @NotNull
    public SessionProposalStatus status;
}
