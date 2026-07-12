package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.*;

import java.util.Collection;

public record SessionProposalDTO(
        String title,
        String description,
        ConferenceSessionFormat conferenceSessionFormat,
        ConferenceTrack conferenceTrack,
        Level level,
        Language language,
        PresenterDTO presenter,
        String presentationOutline,
        Collection<ProgrammingLanguage> programmingLanguagesUsed,
        String preRequisiteKnowledge) {
}
