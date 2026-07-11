package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.*;
import java.util.Collection;
import java.util.Objects;

public record CreateConferenceSessionCommand(
        String title,
        String description,
        Format format,
        Track track,
        Level level,
        Language language,
        EmailAddress presenterEmail,
        String presentationOutline,
        Collection<ProgrammingLanguage> programmingLanguagesUsed,
        String preRequisiteKnowledge) {

    public CreateConferenceSessionCommand {
        Objects.requireNonNull(title, "title is required");
        Objects.requireNonNull(description, "description is required");
        Objects.requireNonNull(format, "format is required");
        Objects.requireNonNull(track, "track is required");
        Objects.requireNonNull(level, "level is required");
        Objects.requireNonNull(language, "language is required");
        Objects.requireNonNull(presenterEmail, "presenterEmail is required");
        Objects.requireNonNull(presentationOutline, "presentationOutline is required");
        Objects.requireNonNull(programmingLanguagesUsed, "programmingLanguagesUsed is required");
        if (programmingLanguagesUsed.isEmpty()) {
            throw new IllegalArgumentException("At least one programming language is required");
        }
    }
}
