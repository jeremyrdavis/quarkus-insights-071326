package io.arrogantprogrammer.quarkusinsights.cfp.infrastructure;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.*;
import jakarta.validation.constraints.*;

import java.util.Collection;

public record ConferenceSessionParameters(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull Format format,
        @NotNull Track track,
        @NotNull Level level,
        @NotNull Language language,
        @Email @NotBlank String presenterEmail,
        @NotBlank String presentationOutline,
        @NotEmpty Collection<ProgrammingLanguage> programmingLanguagesUsed,
        String preRequisiteKnowledge) {
}
