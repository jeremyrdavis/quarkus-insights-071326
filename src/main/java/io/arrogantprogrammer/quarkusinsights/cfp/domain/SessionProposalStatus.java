package io.arrogantprogrammer.quarkusinsights.cfp.domain;

public enum SessionProposalStatus {
    SUBMITTED("Submitted"),
    APPROVED("Approved"),
    DECLINED("Declined"),
    WAITLISTED("Waitlisted");

    public final String displayValue;

    SessionProposalStatus(String displayValue) {
        this.displayValue = displayValue;
    }
}
