package io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceSessionFormat;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.FormatCode;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.Track;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.TrackCode;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CfpTest {

    static final List<Track> ALL_TRACKS = List.of(
            Track.create(TrackCode.JAVA_LANGUAGE, "Java Language", "All things Java language and runtime."),
            Track.create(TrackCode.CLOUD, "Cloud", "Cloud native development, Kubernetes, and serverless."),
            Track.create(TrackCode.DATA_AI, "Data & AI", "Data engineering, machine learning, and artificial intelligence.")
    );

    static final List<ConferenceSessionFormat> ALL_CONFERENCE_SESSION_FORMATS = List.of(
            ConferenceSessionFormat.create(FormatCode.TECHNICAL_SESSION, "Technical session", "A 50-minute presentation-style session featuring in-depth technical content."),
            ConferenceSessionFormat.create(FormatCode.HANDS_ON_LAB, "Hands-on lab (HOL)", "A 120-minute session with self-guided lab instructions for participants who bring their own laptops to experiment with a specific technology. Typically includes one or more instructors for support and troubleshooting."),
            ConferenceSessionFormat.create(FormatCode.KEYNOTE, "Keynote", "A 25-minute keynote delivered to the entire J-Fall audience, typically featuring visionary or thought-leadership content.")
    );

    @Test
    public void testCfpCreation() {

        CfpAggregate cfp = CfpAggregate.create()
                .withConferenceName("J-Fall 2026")
                .withConferenceUrl("https://j-fall.nl")
                .withConferenceDescription("The biggest Java conference in the Netherlands.")
                .withCfpOpens(LocalDate.of(2026, 7, 13))
                .withCfpCloses(LocalDate.of(2026, 8, 13))
                .withFormats(ALL_CONFERENCE_SESSION_FORMATS)
                .withTracks(ALL_TRACKS);

        assertEquals("J-Fall 2026", cfp.getConferenceName());
        assertEquals("https://j-fall.nl", cfp.getConferenceUrl());
        assertEquals("The biggest Java conference in the Netherlands.", cfp.getConferenceDescription());
        assertEquals(LocalDate.of(2026, 7, 13), cfp.getCfpOpens());
        assertEquals(LocalDate.of(2026, 8, 13), cfp.getCfpCloses());
    }



}
