package io.arrogantprogrammer.quarkusinsights.cfp.persistence;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.Language;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.Level;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conference_session")
public class ConferenceSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;

    private String title;

    @Column(length = 2000)
    private String description;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "format_id")
    private FormatEntity format;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "track_id")
    private TrackEntity track;

    @Enumerated(EnumType.STRING)
    private Level level;

    @Enumerated(EnumType.STRING)
    private Language language;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "presenter_id")
    private PresenterEntity presenter;

    private String preRequisiteKnowledge;

    @Column(length = 2000)
    private String presentationOutline;

    @ElementCollection
    @CollectionTable(name = "session_programming_languages", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "programming_language")
    private List<String> programmingLanguagesUsed = new ArrayList<>();

    public ConferenceSessionEntity() {
    }

    public ConferenceSessionEntity(String title, String description, FormatEntity format, TrackEntity track, Level level, Language language, PresenterEntity presenter, String preRequisiteKnowledge, String presentationOutline, List<String> programmingLanguagesUsed) {
        this.title = title;
        this.description = description;
        this.format = format;
        this.track = track;
        this.level = level;
        this.language = language;
        this.presenter = presenter;
        this.preRequisiteKnowledge = preRequisiteKnowledge;
        this.presentationOutline = presentationOutline;
        this.programmingLanguagesUsed = programmingLanguagesUsed;
    }

    public java.util.UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public FormatEntity getFormat() {
        return format;
    }

    public TrackEntity getTrack() {
        return track;
    }

    public Level getLevel() {
        return level;
    }

    public Language getLanguage() {
        return language;
    }

    public PresenterEntity getPresenter() {
        return presenter;
    }

    public String getPreRequisiteKnowledge() {
        return preRequisiteKnowledge;
    }

    public String getPresentationOutline() {
        return presentationOutline;
    }

    public List<String> getProgrammingLanguagesUsed() {
        return programmingLanguagesUsed;
    }
}
