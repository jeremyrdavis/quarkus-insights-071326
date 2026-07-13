package io.arrogantprogrammer.quarkusinsights.communications.infrastructure.email;

import io.arrogantprogrammer.quarkusinsights.communications.application.ports.EmailSender;
import io.arrogantprogrammer.quarkusinsights.communications.domain.valueobjects.EmailMessage;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Quarkus Mailer adapter. Keeps the Quarkus {@link Mail} type inside
 * infrastructure and does not catch mailer exceptions — the delivery workflow
 * records failures for retry.
 */
@ApplicationScoped
public class QuarkusEmailSender implements EmailSender {

    private final Mailer mailer;

    public QuarkusEmailSender(Mailer mailer) {
        this.mailer = mailer;
    }

    @Override
    public void send(EmailMessage message) {
        mailer.send(Mail.withText(message.to(), message.subject(), message.body()));
    }
}
