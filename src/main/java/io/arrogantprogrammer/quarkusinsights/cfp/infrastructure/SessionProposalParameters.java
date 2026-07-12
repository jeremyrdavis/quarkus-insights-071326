package io.arrogantprogrammer.quarkusinsights.cfp.infrastructure;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.*;
import jakarta.validation.constraints.*;

import java.util.Collection;
import java.util.UUID;

public record SessionProposalParameters(
        @NotNull(message = "cfpId is required") UUID cfpId,
        @NotBlank(message = "Title is required") String title,
        @NotBlank(message = "Description is required") String description,
        @NotNull(message = "Format is required") ConferenceSessionFormat conferenceSessionFormat,
        @NotNull(message = "Track is required") ConferenceTrack conferenceTrack,
        @NotNull(message = "Level is required") Level level,
        @NotNull(message = "Language is required") Language language,
        @Email(message = "Invalid email format") @NotBlank(message = "Presenter email is required") String presenterEmail,
        @NotBlank(message = "Presentation outline is required") String presentationOutline,
        @NotEmpty(message = "At least one programming language must be specified") Collection<ProgrammingLanguage> programmingLanguagesUsed,
        String preRequisiteKnowledge) {
}
