package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;

public record PresenterDTO(java.util.UUID id, EmailAddress emailAddress, String firstName, String lastName) {
}
