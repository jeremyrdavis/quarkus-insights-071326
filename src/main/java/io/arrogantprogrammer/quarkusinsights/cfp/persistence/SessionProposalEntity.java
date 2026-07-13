package io.arrogantprogrammer.quarkusinsights.cfp.persistence;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.Language;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.Level;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.SessionProposalStatus;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "session_proposal")
public class SessionProposalEntity {

    @Id
    private UUID id;

    private UUID cfpId;

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

    @Enumerated(EnumType.STRING)
    private SessionProposalStatus status;

    public SessionProposalEntity() {
    }

    public SessionProposalEntity(UUID id, UUID cfpId, String title, String description, FormatEntity format, TrackEntity track, Level level, Language language, PresenterEntity presenter, String preRequisiteKnowledge, String presentationOutline, List<String> programmingLanguagesUsed, SessionProposalStatus status) {
        this.id = id;
        this.cfpId = cfpId;
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
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCfpId() {
        return cfpId;
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

    public void setPresenter(PresenterEntity presenter) {
        this.presenter = presenter;
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

    public SessionProposalStatus getStatus() {
        return status;
    }

    public void setStatus(SessionProposalStatus status) {
        this.status = status;
    }
}
