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

    @Enumerated(EnumType.STRING)
    private FormatCode formatCode;

    private String title;

    private String description;

    private Duration duration;

    public FormatEntity() {
    }

    public FormatEntity(FormatCode formatCode, String title, String description, Duration duration) {
        this.formatCode = formatCode;
        this.title = title;
        this.description = description;
        this.duration = duration;
    }

    public java.util.UUID getId() {
        return id;
    }

    public FormatCode getFormatCode() {
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
