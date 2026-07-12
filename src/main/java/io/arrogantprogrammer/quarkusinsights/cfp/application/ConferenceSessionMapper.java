package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.ConferenceSession;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.ConferenceSessionEntity;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.FormatEntity;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.PresenterEntity;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.TrackEntity;

import java.util.stream.Collectors;

public class ConferenceSessionMapper {
 
    public static ConferenceSessionDTO toDTO(ConferenceSession conferenceSession) {
        return new ConferenceSessionDTO(
                conferenceSession.getTitle(),
                conferenceSession.getDescription(),
                conferenceSession.getFormat(),
                conferenceSession.getTrack(),
                conferenceSession.getLevel(),
                conferenceSession.getLanguage(),
                PresenterMapper.toDTO(conferenceSession.getPresenter()),
                conferenceSession.getPresentationOutline(),
                conferenceSession.getProgrammingLanguagesUsed(),
                conferenceSession.getPreRequisiteKnowledge()
        );
    }
 
    public static ConferenceSessionEntity toEntity(ConferenceSession conferenceSession) {
        return new ConferenceSessionEntity(
                conferenceSession.getId(),
                conferenceSession.getTitle(),
                conferenceSession.getDescription(),
                new FormatEntity(
                        conferenceSession.getFormat().formatCode().name(),
                        conferenceSession.getFormat().title(),
                        conferenceSession.getFormat().description(),
                        conferenceSession.getFormat().duration()
                ),
                new TrackEntity(
                        conferenceSession.getTrack().trackCode(),
                        conferenceSession.getTrack().title(),
                        conferenceSession.getTrack().description()
                ),
                conferenceSession.getLevel(),
                conferenceSession.getLanguage(),
                conferenceSession.getPresenter() == null ? null : new PresenterEntity(
                        conferenceSession.getPresenter().getId(),
                        conferenceSession.getPresenter().getEmail().address(),
                        conferenceSession.getPresenter().getFirstName(),
                        conferenceSession.getPresenter().getLastName()
                ),
                conferenceSession.getPreRequisiteKnowledge(),
                conferenceSession.getPresentationOutline(),
                conferenceSession.getProgrammingLanguagesUsed() == null ? null : conferenceSession.getProgrammingLanguagesUsed().stream().map(pl -> pl.language()).collect(Collectors.toList())
        );
    }
}
