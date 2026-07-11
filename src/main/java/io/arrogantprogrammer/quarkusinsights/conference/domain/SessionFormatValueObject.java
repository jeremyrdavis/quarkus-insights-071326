package io.arrogantprogrammer.quarkusinsights.conference.domain;

import java.time.Duration;
import java.util.Objects;

public record SessionFormatValueObject (
        SessionFormatCode code,
        String displayName,
        Duration duration){

    public SessionFormatValueObject(SessionFormatCode code, String displayName, Duration duration) {
        Objects.requireNonNull(code, "code must not be null");
        Objects.requireNonNull(displayName, "displayName must not be null");
        Objects.requireNonNull(duration, "duration must not be null");
        this.code = code;
        this.displayName = displayName;
        this.duration = duration;
    }
}
