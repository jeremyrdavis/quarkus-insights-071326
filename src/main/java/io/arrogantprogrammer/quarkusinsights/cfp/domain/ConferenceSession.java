package io.arrogantprogrammer.quarkusinsights.cfp.domain;

import io.arrogantprogrammer.quarkusinsights.cfp.application.ConferenceSessionDTO;

import java.util.Collection;

public class ConferenceSession {

    String title;

    String description;

    Format format;

    Track track;

    Level level;

    Language language;
    
    Presenter presenter;

    String preRequisiteKnowledge;

    String presentationOutline;

    Collection<ProgrammingLanguage> programmingLanguagesUsed;

    public static ConferenceSession create() {
        return new ConferenceSession();
    }

    public ConferenceSession withTitle(String title) {
        this.title = title;
        return this;
    }

    public ConferenceSession withAbstractText(String abstractText) {
        this.description = abstractText;
        return this;
    }

    public ConferenceSession withSubmitterAggregate(Presenter presenter) {
        this.presenter = presenter;
        return this;
    }

    public ConferenceSession withFormat(Format format) {
        this.format = format;
        return this;
    }

    public ConferenceSession withTrack(Track track) {
        this.track = track;
        return this;
    }

    public ConferenceSession withLevel(Level level) {
        this.level = level;
        return this;
    }

    public ConferenceSession withLanguage(Language language) {
        this.language = language;
        return this;
    }

    public ConferenceSession withPreRequisiteKnowledge(String preRequisiteKnowledge) {
        this.preRequisiteKnowledge = preRequisiteKnowledge;
        return this;
    }

    public ConferenceSession withPresentationOutline(String presentationOutline) {
        this.presentationOutline = presentationOutline;
        return this;
    }

    public ConferenceSession withProgrammingLanguagesUsed(Collection<ProgrammingLanguage> programmingLanguagesUsed) {
        this.programmingLanguagesUsed = programmingLanguagesUsed;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Format getFormat() {
        return format;
    }

    public Track getTrack() {
        return track;
    }

    public Level getLevel() {
        return level;
    }

    public Language getLanguage() {
        return language;
    }

    public Presenter getPresenter() {
        return presenter;
    }

    public String getPreRequisiteKnowledge() {
        return preRequisiteKnowledge;
    }

    public String getPresentationOutline() {
        return presentationOutline;
    }

    public Collection<ProgrammingLanguage> getProgrammingLanguagesUsed() {
        return programmingLanguagesUsed;
    }

}
