package io.arrogantprogrammer.quarkusinsights.cfp.domain;

import java.util.Objects;
import java.util.regex.Pattern;

public record EmailAddress(String address) {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern PATTERN = Pattern.compile(EMAIL_REGEX);

    public EmailAddress {
        Objects.requireNonNull(address, "Email address cannot be null");
        if (address.isBlank()) {
            throw new IllegalArgumentException("Email address cannot be blank");
        }
        if (!PATTERN.matcher(address).matches()) {
            throw new IllegalArgumentException("Invalid email address format: " + address);
        }
    }

    @Override
    public String toString() {
        return address;
    }
}
