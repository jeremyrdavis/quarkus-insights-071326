package io.arrogantprogrammer.quarkusinsights.cfp.infrastructure;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

public record PresenterParameters(
        @Email(message = "Invalid email format") @NotBlank(message = "Email is required") String email,
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Last name is required") String lastName) {
}

