package io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CfpTest {

    static final EmailAddress CONTACT_EMAIL = new EmailAddress("info@nljug.org");

    static final List<ConferenceTrack> ALL_CONFERENCE_TRACKS = List.of(
            ConferenceTrack.create(TrackCode.JAVA_LANGUAGE, "Java Language", "All things Java language and runtime."),
            ConferenceTrack.create(TrackCode.CLOUD, "Cloud", "Cloud native development, Kubernetes, and serverless."),
            ConferenceTrack.create(TrackCode.DATA_AI, "Data & AI", "Data engineering, machine learning, and artificial intelligence.")
    );

    static final List<ConferenceSessionFormat> ALL_CONFERENCE_SESSION_FORMATS = List.of(
            new ConferenceSessionFormat(FormatCode.TECHNICAL_SESSION, "Technical session", "A 50-minute presentation-style session featuring in-depth technical content.", Duration.ofMinutes(50)),
            new ConferenceSessionFormat(FormatCode.HANDS_ON_LAB, "Hands-on lab (HOL)", "A 120-minute session with self-guided lab instructions for participants who bring their own laptops to experiment with a specific technology. Typically includes one or more instructors for support and troubleshooting.", Duration.ofMinutes(120)),
            new ConferenceSessionFormat(FormatCode.KEYNOTE, "Keynote", "A 25-minute keynote delivered to the entire J-Fall audience, typically featuring visionary or thought-leadership content.", Duration.ofMinutes(25))
    );

    @Test
    public void testCfpCreation() {

        Cfp cfp = Cfp.create(
                LocalDate.of(2026, 7, 13),
                LocalDate.of(2026, 8, 13),
                "J-Fall 2026",
                "https://j-fall.nl",
                "The biggest Java conference in the Netherlands.",
                ALL_CONFERENCE_SESSION_FORMATS,
                ALL_CONFERENCE_TRACKS,
                CONTACT_EMAIL);

        assertEquals("J-Fall 2026", cfp.getConferenceName());
        assertEquals("https://j-fall.nl", cfp.getConferenceUrl());
        assertEquals("The biggest Java conference in the Netherlands.", cfp.getConferenceDescription());
        assertEquals(LocalDate.of(2026, 7, 13), cfp.getCfpOpens());
        assertEquals(LocalDate.of(2026, 8, 13), cfp.getCfpCloses());
        assertEquals(CONTACT_EMAIL, cfp.getContactEmailAddress());
    }



}
