package io.arrogantprogrammer.quarkusinsights.cfp.domain;

import java.time.Duration;
import java.util.Objects;

public record ConferenceSessionFormat(
        FormatCode formatCode,
        String title,
        String description,
        Duration duration) {

    public static ConferenceSessionFormat create(FormatCode formatCode, String title, String description, Duration duration) {
        Objects.requireNonNull(title, "Title cannot be null");
        Objects.requireNonNull(description, "Description cannot be null");
        Objects.requireNonNull(formatCode, "Format code cannot be null");
        Objects.requireNonNull(duration, "Duration cannot be null");
        return new ConferenceSessionFormat(formatCode, title, description, duration);
    }
}
