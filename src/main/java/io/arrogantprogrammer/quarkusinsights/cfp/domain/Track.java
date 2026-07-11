package io.arrogantprogrammer.quarkusinsights.cfp.domain;

import java.util.List;
import java.util.Objects;
 
public record Track(
        TrackCode trackCode,
        String title,
        String description) {

    public static Track create(TrackCode trackCode, String title, String description) {
        Objects.requireNonNull(trackCode, "Track code cannot be null");
        Objects.requireNonNull(title, "Title cannot be null");
        Objects.requireNonNull(description, "Description cannot be null");
        return new Track(trackCode, title, description);
    }
}
