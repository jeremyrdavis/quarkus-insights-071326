package io.arrogantprogrammer.quarkusinsights.cfp.domain;

import java.util.Objects;
 
public record ConferenceTrack(
        TrackCode trackCode,
        String title,
        String description) {

    public static ConferenceTrack create(TrackCode trackCode, String title, String description) {
        Objects.requireNonNull(trackCode, "Track code cannot be null");
        Objects.requireNonNull(title, "Title cannot be null");
        Objects.requireNonNull(description, "Description cannot be null");
        return new ConferenceTrack(trackCode, title, description);
    }
}
