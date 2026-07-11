package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.*;

import java.util.Collection;

public record ConferenceSessionDTO(
        String title,
        String description,
        ConferenceSessionFormat conferenceSessionFormat,
        Track track,
        Level level,
        Language language,
        PresenterDTO presenter,
        String presentationOutline,
        Collection<ProgrammingLanguage> programmingLanguagesUsed,
        String preRequisiteKnowledge) {
}
