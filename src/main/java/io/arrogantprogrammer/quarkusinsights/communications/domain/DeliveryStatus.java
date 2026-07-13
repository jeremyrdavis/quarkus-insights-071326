package io.arrogantprogrammer.quarkusinsights.communications.domain;

public enum DeliveryStatus {
    PENDING,
    PROCESSING,
    DELIVERED,
    RETRY_SCHEDULED,
    PERMANENTLY_FAILED
}
