package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.CfpAggregate;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.ConferenceSession;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.Presenter;
import io.arrogantprogrammer.quarkusinsights.cfp.infrastructure.PresenterParameters;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.ConferenceSessionEntity;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.ConferenceSessionRepository;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;


@ApplicationScoped
public class CfpService {

    @Inject
    ConferenceSessionRepository conferenceSessionRepository;

    public PresenterDTO registerPresenter(CreatePresenterCommand command){
        Presenter presenter = Presenter.create().withEmail(command.email()).withFirstName(command.firstName()).withLastName(command.lastName());
        return PresenterMapper.toDTO(presenter);
    }

    public PresenterDTO getPresenter(String email) {
        return null;
    }

    public PresenterDTO updatePresenter(String email, PresenterParameters parameters) {
        return null;
    }

    public void deletePresenter(String email) {

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
                .withFormat(command.format())
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
