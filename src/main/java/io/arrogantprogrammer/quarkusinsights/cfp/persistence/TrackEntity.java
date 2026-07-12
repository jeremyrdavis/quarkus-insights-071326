package io.arrogantprogrammer.quarkusinsights.cfp.persistence;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.TrackCode;
import jakarta.persistence.*;

@Entity
@Table(name = "track")
public class TrackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;

    private String trackCode;

    private String title;

    private String description;

    public TrackEntity() {
    }

    public TrackEntity(String trackCode, String title, String description) {
        this.trackCode = trackCode;
        this.title = title;
        this.description = description;
    }

    public java.util.UUID getId() {
        return id;
    }

    public String getTrackCode() {
        return trackCode;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
