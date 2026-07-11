package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.*;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.Cfp;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CfpMapperTest {

    @Test
    public void testToEntity() {
        Cfp cfp = Cfp.create(
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                "Quarkus Insights",
                "https://quarkus.io",
                "Conference about Quarkus",
                List.of(ConferenceSessionFormat.create(FormatCode.TECHNICAL_SESSION, "Technical Session", "A session about technology")),
                List.of(Track.create(TrackCode.ARCHITECTURE, "Architecture", "Architecture track")),
                new EmailAddress("info@quarkus.io"));

        CfpDTO cfpDTO = CfpMapper.toDTO(cfp);

        assertNotNull(cfpDTO);
        assertNotNull(cfpDTO.id());
        assertEquals(cfp.getCfpOpens(), cfpDTO.cfpOpens());
        assertEquals(cfp.getCfpCloses(), cfpDTO.cfpCloses());
        assertEquals(cfp.getConferenceName(), cfpDTO.conferenceName());
        assertEquals(cfp.getConferenceUrl(), cfpDTO.conferenceUrl());
        assertEquals(cfp.getConferenceDescription(), cfpDTO.conferenceDescription());
        assertEquals(cfp.getContactEmailAddress().address(), cfpDTO.contactEmailAddress().address());
        
        assertEquals(1, cfpDTO.conferenceSessionFormats().size());
        assertEquals(FormatCode.TECHNICAL_SESSION, cfpDTO.conferenceSessionFormats().get(0).formatCode());
        assertEquals("Technical Session", cfpDTO.conferenceSessionFormats().get(0).title());
        
        assertEquals(1, cfpDTO.tracks().size());
        assertEquals(TrackCode.ARCHITECTURE, cfpDTO.tracks().get(0).trackCode());
        assertEquals("Architecture", cfpDTO.tracks().get(0).title());
    }
}
