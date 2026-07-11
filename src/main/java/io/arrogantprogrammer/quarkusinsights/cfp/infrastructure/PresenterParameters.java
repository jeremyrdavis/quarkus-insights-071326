package io.arrogantprogrammer.quarkusinsights.cfp.infrastructure;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record PresenterParameters(
        @Email @NotEmpty String email,
        @NotEmpty String firstName,
        @NotEmpty String lastName) {
}

