package io.arrogantprogrammer.quarkusinsights.cfp.persistence.outbox;

public enum CfpOutboxStatus {
    PENDING,
    PUBLISHED,
    FAILED
}
