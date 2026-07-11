package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.*;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.Cfp;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.CfpEntity;
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

        CfpEntity entity = CfpMapper.toEntity(cfp);

        assertNotNull(entity);
        assertEquals(cfp.getCfpOpens(), entity.getCfpOpens());
        assertEquals(cfp.getCfpCloses(), entity.getCfpCloses());
        assertEquals(cfp.getConferenceName(), entity.getConferenceName());
        assertEquals(cfp.getConferenceUrl(), entity.getConferenceUrl());
        assertEquals(cfp.getConferenceDescription(), entity.getConferenceDescription());
        assertEquals(cfp.getContactEmailAddress().address(), entity.getContactEmailAddress());
        
        assertEquals(1, entity.getConferenceSessionFormats().size());
        assertEquals(FormatCode.TECHNICAL_SESSION, entity.getConferenceSessionFormats().get(0).getFormatCode());
        assertEquals("Technical Session", entity.getConferenceSessionFormats().get(0).getTitle());
        
        assertEquals(1, entity.getTracks().size());
        assertEquals(TrackCode.ARCHITECTURE, entity.getTracks().get(0).getTrackCode());
        assertEquals("Architecture", entity.getTracks().get(0).getTitle());
    }
}
