package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.CfpAggregate;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.ConferenceSession;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.Presenter;
import io.arrogantprogrammer.quarkusinsights.cfp.infrastructure.PresenterParameters;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.*;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Optional;


@ApplicationScoped
public class CfpService {

    @Inject
    ConferenceSessionRepository conferenceSessionRepository;

    @Inject
    PresenterRepository presenterRepository;

    @Transactional
    public PresenterDTO registerPresenter(CreatePresenterCommand command){
        Presenter presenter = Presenter.create().withEmail(command.email()).withFirstName(command.firstName()).withLastName(command.lastName());
        PresenterEntity presenterEntity = PresenterMapper.toEntity(presenter);
        presenterRepository.persist(presenterEntity);
        return PresenterMapper.toDTO(presenter);
    }

    public PresenterDTO getPresenter(String email) {
        Optional<PresenterEntity> presenterEntity = presenterRepository.findByEmail(email);
        return presenterEntity.map(entity -> PresenterMapper.toDTO(PresenterMapper.toDomain(entity))).orElse(null);
    }

    @Transactional
    public PresenterDTO updatePresenter(String email, PresenterParameters parameters) {
        Optional<PresenterEntity> presenterEntityOptional = presenterRepository.findByEmail(email);
        if (presenterEntityOptional.isPresent()) {
            PresenterEntity presenterEntity = presenterEntityOptional.get();
            presenterEntity.setFirstName(parameters.firstName());
            presenterEntity.setLastName(parameters.lastName());
            return PresenterMapper.toDTO(PresenterMapper.toDomain(presenterEntity));
        }
        return null;
    }

    @Transactional
    public void deletePresenter(String email) {
        presenterRepository.delete("email", email);
    }

    public CfpDTO createCfp(CreateCfpCommand command) {
        CfpAggregate cfpAggregate = CfpAggregate.create().withCfpOpens(command.cfpOpens()).withCfpCloses(command.cfpCloses());
        return CfpMapper.toDTO(cfpAggregate);
    }

    @Transactional
    public ConferenceSessionDTO createConferenceSession(CreateConferenceSessionCommand command) {
        Log.debugf("createConferenceSession: {}", command);

        ConferenceSession conferenceSession = ConferenceSession.create()
                .withTitle(command.title())
                .withAbstractText(command.description())
                .withFormat(command.conferenceSessionFormat())
                .withTrack(command.track())
                .withLevel(command.level())
                .withLanguage(command.language())
                .withPresentationOutline(command.presentationOutline())
                .withProgrammingLanguagesUsed(command.programmingLanguagesUsed())
                .withPreRequisiteKnowledge(command.preRequisiteKnowledge());
        Log.debugf("createConferenceSession: {}", conferenceSession);

        // Persisting the conference session
        ConferenceSessionEntity conferenceSessionEntity = ConferenceSessionMapper.toEntity(conferenceSession);
        Log.debugf("persisting : {}", conferenceSessionEntity);
        conferenceSessionRepository.persist(conferenceSessionEntity);
        Log.debugf("persisted : {}", conferenceSessionEntity);

        // Returning the conference session DTO
        return ConferenceSessionMapper.toDTO(conferenceSession);
    }
}
