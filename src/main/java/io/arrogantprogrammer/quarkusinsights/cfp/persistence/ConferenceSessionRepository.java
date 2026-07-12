package io.arrogantprogrammer.quarkusinsights.cfp.persistence;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceSessionFormat;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceTrack;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.FormatCode;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.ProgrammingLanguage;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.ConferenceSession;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.Presenter;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class ConferenceSessionRepository implements PanacheRepository<ConferenceSessionEntity> {

    public List<ConferenceSession> findSessionProposalsByPresenterId(UUID presenterId) {
        return list("presenter.id", presenterId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private ConferenceSession toDomain(ConferenceSessionEntity entity) {
        return new ConferenceSession(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                new ConferenceSessionFormat(
                        FormatCode.valueOf(entity.getFormat().getFormatCode()),
                        entity.getFormat().getTitle(),
                        entity.getFormat().getDescription(),
                        entity.getFormat().getDuration()),
                new ConferenceTrack(
                        entity.getTrack().getTrackCode(),
                        entity.getTrack().getTitle(),
                        entity.getTrack().getDescription()),
                entity.getLevel(),
                entity.getLanguage(),
                new Presenter(
                        entity.getPresenter().getId(),
                        new EmailAddress(entity.getPresenter().getEmail()),
                        entity.getPresenter().getFirstName(),
                        entity.getPresenter().getLastName()),
                entity.getPreRequisiteKnowledge(),
                entity.getPresentationOutline(),
                entity.getProgrammingLanguagesUsed().stream()
                        .map(ProgrammingLanguage::new)
                        .collect(Collectors.toList())
        );
    }
}
