package io.arrogantprogrammer.quarkusinsights.cfp.persistence;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.FormatCode;
import jakarta.persistence.*;
import java.time.Duration;

@Entity
@Table(name = "format")
public class FormatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;

    private String formatCode;

    private String title;

    private String description;

    private Duration duration;

    public FormatEntity() {
    }

    public FormatEntity(String formatCode, String title, String description, Duration duration) {
        this.formatCode = formatCode;
        this.title = title;
        this.description = description;
        this.duration = duration;
    }

    public java.util.UUID getId() {
        return id;
    }

    public String getFormatCode() {
        return formatCode;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Duration getDuration() {
        return duration;
    }
}
