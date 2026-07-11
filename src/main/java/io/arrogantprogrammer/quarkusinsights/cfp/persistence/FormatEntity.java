package io.arrogantprogrammer.quarkusinsights.cfp.persistence;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.FormatCode;
import jakarta.persistence.*;
import java.time.Duration;

@Entity
@Table(name = "format")
public class FormatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "format_seq")
    @SequenceGenerator(name = "format_seq", sequenceName = "format_sequence")
    private Long id;

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

    public Long getId() {
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
