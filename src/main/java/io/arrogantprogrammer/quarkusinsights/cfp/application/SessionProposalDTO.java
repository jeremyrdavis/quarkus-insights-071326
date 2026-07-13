package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.*;

import java.util.Collection;
import java.util.UUID;

public record SessionProposalDTO(
        UUID id,
        UUID cfpId,
        String title,
        String description,
        ConferenceSessionFormat conferenceSessionFormat,
        ConferenceTrack conferenceTrack,
        Level level,
        Language language,
        PresenterDTO presenter,
        String presentationOutline,
        Collection<ProgrammingLanguage> programmingLanguagesUsed,
        String preRequisiteKnowledge,
        SessionProposalStatus status) {
}
