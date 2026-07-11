package io.arrogantprogrammer.quarkusinsights.cfp.domain;

import java.time.Duration;
import java.util.Objects;

public record ConferenceSessionFormat(
        FormatCode formatCode,
        String title,
        String description,
        Duration duration) {

    public static ConferenceSessionFormat create(FormatCode formatCode, String title, String description) {
        Objects.requireNonNull(title, "Title cannot be null");
        Objects.requireNonNull(description, "Description cannot be null");
        Objects.requireNonNull(formatCode, "Format code cannot be null");
        Duration duration;
        if(formatCode == FormatCode.HANDS_ON_LAB)
            duration = Duration.ofHours(2);
        else if (formatCode == FormatCode.KEYNOTE) {
            duration = Duration.ofMinutes(25);
        } else if (formatCode == FormatCode.IGNITE) {
            duration = Duration.ofMinutes(5);
        } else if (formatCode == FormatCode.BYTE_SIZE) {
            duration = Duration.ofMinutes(16);
        } else if (formatCode == FormatCode.PRE_CONFERENCE_WORKSHOP) {
            duration = Duration.ofHours(4);
        } else {
            duration = Duration.ofMinutes(50);
        }
        return new ConferenceSessionFormat(formatCode, title, description, duration);
    }
}
