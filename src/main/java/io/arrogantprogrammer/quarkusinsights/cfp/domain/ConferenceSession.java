package io.arrogantprogrammer.quarkusinsights.cfp.domain;

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
}
