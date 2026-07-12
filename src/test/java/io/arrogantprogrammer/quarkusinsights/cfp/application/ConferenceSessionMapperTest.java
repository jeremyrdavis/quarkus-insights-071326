package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.*;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.ConferenceSession;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.Presenter;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.ConferenceSessionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConferenceSessionMapperTest {

    private ConferenceSessionMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = new ConferenceSessionMapper();
    }

    @Test
    public void testToDTO() {
        ConferenceSession session = createTestSession();
        ConferenceSessionDTO dto = mapper.toDTO(session);

        assertNotNull(dto);
        assertEquals(session.getTitle(), dto.title());
        assertEquals(session.getDescription(), dto.description());
        assertEquals(session.getFormat(), dto.conferenceSessionFormat());
        assertEquals(session.getTrack(), dto.conferenceTrack());
        assertEquals(session.getLevel(), dto.level());
        assertEquals(session.getLanguage(), dto.language());
        assertEquals(PresenterMapper.toDTO(session.getPresenter()), dto.presenter());
        assertEquals(session.getPresentationOutline(), dto.presentationOutline());
        assertEquals(session.getProgrammingLanguagesUsed(), dto.programmingLanguagesUsed());
        assertEquals(session.getPreRequisiteKnowledge(), dto.preRequisiteKnowledge());
    }

    @Test
    public void testToEntity() {
        ConferenceSession session = createTestSession();
        ConferenceSessionEntity entity = mapper.toEntity(session);

        assertNotNull(entity);
        assertEquals(session.getId(), entity.getId());
        assertEquals(session.getTitle(), entity.getTitle());
        assertEquals(session.getDescription(), entity.getDescription());
        assertEquals(session.getFormat().formatCode(), entity.getFormat().getFormatCode());
        assertEquals(session.getFormat().title(), entity.getFormat().getTitle());
        assertEquals(session.getTrack().trackCode(), entity.getTrack().getTrackCode());
        assertEquals(session.getTrack().title(), entity.getTrack().getTitle());
        assertEquals(session.getLevel(), entity.getLevel());
        assertEquals(session.getLanguage(), entity.getLanguage());
        assertEquals(session.getPresenter().getId(), entity.getPresenter().getId());
        assertEquals(session.getPresenter().getEmail().address(), entity.getPresenter().getEmail());
        assertEquals(session.getPresenter().getFirstName(), entity.getPresenter().getFirstName());
        assertEquals(session.getPresenter().getLastName(), entity.getPresenter().getLastName());
        assertEquals(session.getPreRequisiteKnowledge(), entity.getPreRequisiteKnowledge());
        assertEquals(session.getPresentationOutline(), entity.getPresentationOutline());
        assertEquals(1, entity.getProgrammingLanguagesUsed().size());
        assertEquals("Java", entity.getProgrammingLanguagesUsed().get(0));
    }

    private ConferenceSession createTestSession() {
        Presenter presenter = Presenter.create(new EmailAddress("steve@example.com"), "Steve", "Jobs");
        
        return ConferenceSession.create(
                "Mapping Sessions",
                "Abstract",
                ConferenceSessionFormat.create(FormatCode.TECHNICAL_SESSION, "Technical", "Description"),
                ConferenceTrack.create(TrackCode.ARCHITECTURE, "Arch", "Desc"),
                Level.BEGINNER,
                Language.ENGLISH,
                presenter,
                "None",
                "Outline",
                List.of(new ProgrammingLanguage("Java")));
    }
}
