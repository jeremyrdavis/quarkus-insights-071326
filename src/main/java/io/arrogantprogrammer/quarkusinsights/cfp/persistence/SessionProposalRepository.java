package io.arrogantprogrammer.quarkusinsights.cfp.persistence;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceSessionFormat;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceTrack;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.FormatCode;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.ProgrammingLanguage;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.Presenter;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.SessionProposal;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class SessionProposalRepository implements PanacheRepository<SessionProposalEntity> {

    public List<SessionProposal> findSessionProposalsByPresenterId(UUID presenterId) {
        return list("presenter.id", presenterId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public Optional<SessionProposal> findById(UUID id) {
        return find("id", id).<SessionProposalEntity>firstResultOptional()
                .map(this::toDomain);
    }

    public List<SessionProposal> findByCfpId(UUID cfpId) {
        return list("cfpId", cfpId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Transactional
    public SessionProposal save(SessionProposal proposal) {
        SessionProposalEntity entity = find("id", proposal.getId()).<SessionProposalEntity>firstResult();
        if (entity == null) {
            throw new jakarta.ws.rs.NotFoundException("SessionProposal not found: " + proposal.getId());
        }
        entity.setStatus(proposal.getStatus());
        return toDomain(entity);
    }

    private SessionProposal toDomain(SessionProposalEntity entity) {
        return new SessionProposal(
                entity.getId(),
                entity.getCfpId(),
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
                entity.getPresenter() == null ? null : new Presenter(
                        entity.getPresenter().getId(),
                        new EmailAddress(entity.getPresenter().getEmail()),
                        entity.getPresenter().getFirstName(),
                        entity.getPresenter().getLastName()),
                entity.getPreRequisiteKnowledge(),
                entity.getPresentationOutline(),
                entity.getProgrammingLanguagesUsed().stream()
                        .map(ProgrammingLanguage::new)
                        .collect(Collectors.toList()),
                entity.getStatus()
        );
    }
}
