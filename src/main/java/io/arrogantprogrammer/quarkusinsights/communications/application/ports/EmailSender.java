package io.arrogantprogrammer.quarkusinsights.communications.application.ports;

import io.arrogantprogrammer.quarkusinsights.communications.domain.valueobjects.EmailMessage;

/**
 * Outbound port for sending an email. Implemented by an infrastructure adapter.
 * Implementations must NOT swallow send failures — the delivery workflow records
 * them for retry.
 */
public interface EmailSender {

    void send(EmailMessage message);
}
