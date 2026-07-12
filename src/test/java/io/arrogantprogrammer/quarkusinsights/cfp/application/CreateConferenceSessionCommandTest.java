package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreateConferenceSessionCommandTest {

    @Test
    public void testValidation() {
        EmailAddress email = new EmailAddress("test@example.com");
        ProgrammingLanguage java = new ProgrammingLanguage("Java");
        ConferenceSessionFormat conferenceSessionFormat = new ConferenceSessionFormat(FormatCode.TECHNICAL_SESSION, "Technical Session", "A technical session", Duration.ofMinutes(50));
        ConferenceTrack conferenceTrack = new ConferenceTrack(TrackCode.ARCHITECTURE, "Architecture", "Architecture track");

        // Test null values (Objects.requireNonNull throws NullPointerException)
        assertThrows(NullPointerException.class, () -> new CreateConferenceSessionCommand(null, "desc", conferenceSessionFormat, conferenceTrack, Level.BEGINNER, Language.ENGLISH, email, "outline", List.of(java), null, ));
        assertThrows(NullPointerException.class, () -> new CreateConferenceSessionCommand("title", null, conferenceSessionFormat, conferenceTrack, Level.BEGINNER, Language.ENGLISH, email, "outline", List.of(java), null, ));
        assertThrows(NullPointerException.class, () -> new CreateConferenceSessionCommand("title", "desc", null, conferenceTrack, Level.BEGINNER, Language.ENGLISH, email, "outline", List.of(java), null, ));
        assertThrows(NullPointerException.class, () -> new CreateConferenceSessionCommand("title", "desc", conferenceSessionFormat, null, Level.BEGINNER, Language.ENGLISH, email, "outline", List.of(java), null, ));
        assertThrows(NullPointerException.class, () -> new CreateConferenceSessionCommand("title", "desc", conferenceSessionFormat, conferenceTrack, null, Language.ENGLISH, email, "outline", List.of(java), null, ));
        assertThrows(NullPointerException.class, () -> new CreateConferenceSessionCommand("title", "desc", conferenceSessionFormat, conferenceTrack, Level.BEGINNER, null, email, "outline", List.of(java), null, ));
        assertThrows(NullPointerException.class, () -> new CreateConferenceSessionCommand("title", "desc", conferenceSessionFormat, conferenceTrack, Level.BEGINNER, Language.ENGLISH, null, "outline", List.of(java), null, ));
        assertThrows(NullPointerException.class, () -> new CreateConferenceSessionCommand("title", "desc", conferenceSessionFormat, conferenceTrack, Level.BEGINNER, Language.ENGLISH, email, null, List.of(java), null, ));
        assertThrows(NullPointerException.class, () -> new CreateConferenceSessionCommand("title", "desc", conferenceSessionFormat, conferenceTrack, Level.BEGINNER, Language.ENGLISH, email, "outline", null, null, ));

        // Test empty programming languages (throws IllegalArgumentException)
        assertThrows(IllegalArgumentException.class, () -> new CreateConferenceSessionCommand("title", "desc", conferenceSessionFormat, conferenceTrack, Level.BEGINNER, Language.ENGLISH, email, "outline", Collections.emptyList(), null, ));
    }
}
