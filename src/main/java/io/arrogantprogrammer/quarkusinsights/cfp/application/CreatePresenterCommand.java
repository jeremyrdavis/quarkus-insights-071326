package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;

public record CreatePresenterCommand(EmailAddress email, String firstName, String lastName) {
}
