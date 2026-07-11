package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.Cfp;
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
    CfpRepository cfpRepository;

    @Inject
    ConferenceSessionRepository conferenceSessionRepository;

    @Inject
    PresenterRepository presenterRepository;

    @Transactional
    public PresenterDTO registerPresenter(CreatePresenterCommand command){
        Presenter presenter = Presenter.create(command.email(), command.firstName(), command.lastName());
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
        Log.debugf("createCfp: {}", command);
        Cfp cfp = Cfp.create(
                command.cfpOpens(),
                command.cfpCloses(),
                command.conferenceName(),
                command.conferenceUrl(),
                command.conferenceDescription(),
                command.conferenceSessionFormats(),
                command.tracks(),
                command.contactEmailAddress());
        CfpEntity cfpEntity = CfpMapper.toEntity(cfp);
        cfpRepository.persist(cfpEntity);
        return CfpMapper.toDTO(cfp);
    }

    @Transactional
    public ConferenceSessionDTO createConferenceSession(CreateConferenceSessionCommand command) {
        Log.debugf("createConferenceSession: {}", command);

        ConferenceSession conferenceSession = ConferenceSession.create(
                command.title(),
                command.description(),
                command.conferenceSessionFormat(),
                command.track(),
                command.level(),
                command.language(),
                null,
                command.preRequisiteKnowledge(),
                command.presentationOutline(),
                command.programmingLanguagesUsed());
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
