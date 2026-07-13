package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.SessionProposal;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.FormatEntity;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.PresenterEntity;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.SessionProposalEntity;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.TrackEntity;

import java.util.stream.Collectors;

public class SessionProposalMapper {

    public static SessionProposalDTO toDTO(SessionProposal sessionProposal) {
        return new SessionProposalDTO(
                sessionProposal.getId(),
                sessionProposal.getCfpId(),
                sessionProposal.getTitle(),
                sessionProposal.getDescription(),
                sessionProposal.getFormat(),
                sessionProposal.getTrack(),
                sessionProposal.getLevel(),
                sessionProposal.getLanguage(),
                PresenterMapper.toDTO(sessionProposal.getPresenter()),
                sessionProposal.getPresentationOutline(),
                sessionProposal.getProgrammingLanguagesUsed(),
                sessionProposal.getPreRequisiteKnowledge(),
                sessionProposal.getStatus()
        );
    }

    public static SessionProposalEntity toEntity(SessionProposal sessionProposal) {
        return new SessionProposalEntity(
                sessionProposal.getId(),
                sessionProposal.getCfpId(),
                sessionProposal.getTitle(),
                sessionProposal.getDescription(),
                new FormatEntity(
                        sessionProposal.getFormat().formatCode().name(),
                        sessionProposal.getFormat().title(),
                        sessionProposal.getFormat().description(),
                        sessionProposal.getFormat().duration()
                ),
                new TrackEntity(
                        sessionProposal.getTrack().trackCode(),
                        sessionProposal.getTrack().title(),
                        sessionProposal.getTrack().description()
                ),
                sessionProposal.getLevel(),
                sessionProposal.getLanguage(),
                sessionProposal.getPresenter() == null ? null : new PresenterEntity(
                        sessionProposal.getPresenter().getId(),
                        sessionProposal.getPresenter().getEmail().address(),
                        sessionProposal.getPresenter().getFirstName(),
                        sessionProposal.getPresenter().getLastName()
                ),
                sessionProposal.getPreRequisiteKnowledge(),
                sessionProposal.getPresentationOutline(),
                sessionProposal.getProgrammingLanguagesUsed() == null ? null :
                        sessionProposal.getProgrammingLanguagesUsed().stream()
                                .map(pl -> pl.language())
                                .collect(Collectors.toList()),
                sessionProposal.getStatus()
        );
    }
}
