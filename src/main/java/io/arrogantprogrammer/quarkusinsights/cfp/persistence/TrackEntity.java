package io.arrogantprogrammer.quarkusinsights.cfp.persistence;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.TrackCode;
import jakarta.persistence.*;

@Entity
@Table(name = "track")
public class TrackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "track_seq")
    @SequenceGenerator(name = "track_seq", sequenceName = "track_sequence")
    private Long id;

    @Enumerated(EnumType.STRING)
    private TrackCode trackCode;

    private String title;

    private String description;

    public TrackEntity() {
    }

    public TrackEntity(TrackCode trackCode, String title, String description) {
        this.trackCode = trackCode;
        this.title = title;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public TrackCode getTrackCode() {
        return trackCode;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
