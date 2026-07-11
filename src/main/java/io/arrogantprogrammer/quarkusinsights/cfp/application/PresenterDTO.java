package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;

public record PresenterDTO(EmailAddress emailAddress, String firstName, String lastName) {
}
