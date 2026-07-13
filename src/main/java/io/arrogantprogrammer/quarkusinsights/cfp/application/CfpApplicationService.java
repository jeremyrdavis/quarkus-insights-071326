package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.SubmissionContext;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.Cfp;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.Presenter;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.SessionProposal;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.events.SessionProposalAcceptedEvent;
import io.arrogantprogrammer.quarkusinsights.cfp.infrastructure.PresenterParameters;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.*;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.outbox.CfpOutboxEventRepository;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@ApplicationScoped
public class CfpApplicationService {

    @Inject
    CfpRepository cfpRepository;

    @Inject
    SessionProposalRepository sessionProposalRepository;

    @Inject
    PresenterRepository presenterRepository;

    @Inject
    SubmissionProposalApplicationService submissionProposalApplicationService;

    @Inject
    CfpOutboxEventRepository outboxRepository;

    @Transactional
    public PresenterDTO registerPresenter(CreatePresenterCommand command){
        Presenter presenter = Presenter.create(command.email(), command.firstName(), command.lastName());
        Presenter persistedPresenter = presenterRepository.register(presenter);
        return PresenterMapper.toDTO(persistedPresenter);
    }

    public Optional<PresenterDTO> getPresenter(String email) {
        Optional<Presenter> presenter = presenterRepository.findByEmail(email);
        if(presenter.isPresent()){
            return Optional.of(PresenterMapper.toDTO(presenter.get()));
        }else {
            return Optional.empty();
        }
    }

    @Transactional
    public PresenterDTO updatePresenter(String email, PresenterParameters parameters) {
        Optional<Presenter> presenter = presenterRepository.findByEmail(email);
        if (presenter.isPresent()) {
            return PresenterMapper.toDTO(presenter.get());
        }
        return null;
    }

    @Transactional
    public void deletePresenter(String email) {
        presenterRepository.delete("email", email);
    }

    public Optional<CfpDTO> getCfp(UUID id) {
        return cfpRepository.findByUUID(id).map(CfpMapper::toDTO);
    }

    public List<CfpDTO> getAllCfps() {
        return cfpRepository.findAllCfps().stream().map(CfpMapper::toDTO).toList();
    }

    @Transactional
    public Optional<CfpDTO> updateCfp(UUID id, UpdateCfpCommand command) {
        return cfpRepository.updateCfp(id, command).map(CfpMapper::toDTO);
    }

    @Transactional
    public boolean deleteCfp(UUID id) {
        return cfpRepository.deleteCfp(id);
    }

    @Transactional
    public CfpDTO createCfp(CreateCfpCommand command) {
        Log.debugf("createCfp: {}", command);
        Cfp cfp = Cfp.create(
                command.cfpOpens(),
                command.cfpCloses(),
                command.conferenceName(),
                command.conferenceUrl(),
                command.conferenceDescription(),
                command.conferenceSessionFormats(),
                command.conferenceTracks(),
                command.contactEmailAddress());
        Cfp persistedCfp = cfpRepository.createCfp(cfp);
        return CfpMapper.toDTO(persistedCfp);
    }

    @Transactional
    public SessionProposalDTO createSessionProposal(CreateSessionProposalCommand command) {
        Log.debugf("createSessionProposal: {}", command);
        SubmissionContext submissionContext = submissionProposalApplicationService.getSubmissionContext(command.cfpId(), null);
        // Resolve the presenter so the proposal — and any downstream acceptance
        // event — always carries the presenter identity and email.
        Presenter presenter = presenterRepository.findByEmail(command.presenterEmail().address())
                .orElseThrow(() -> new NotFoundException(
                        "Presenter not found for email: " + command.presenterEmail().address()));
        SessionProposal sessionProposal = SessionProposal.create(
                command.cfpId(),
                submissionContext,
                command.title(),
                command.description(),
                command.conferenceSessionFormat(),
                command.conferenceTrack(),
                command.level(),
                command.language(),
                presenter,
                command.preRequisiteKnowledge(),
                command.presentationOutline(),
                command.programmingLanguagesUsed());
        Log.debugf("createSessionProposal: {}", sessionProposal);

        Log.debugf("persisting : {}", sessionProposal);
        sessionProposalRepository.create(sessionProposal);
        Log.debugf("persisted : {}", sessionProposal);

        return SessionProposalMapper.toDTO(sessionProposal);
    }

    public List<SessionProposalDTO> getSessionProposalsForCfp(UUID cfpId) {
        return sessionProposalRepository.findByCfpId(cfpId)
                .stream().map(SessionProposalMapper::toDTO).toList();
    }

    /**
     * Review a proposal. Acceptance is the only status change that produces a
     * published event: the aggregate update and the outbox insert commit
     * together (or roll back together). No CDI event is fired here — the outbox
     * publisher does that asynchronously and durably.
     */
    @Transactional
    public SessionProposalDTO reviewSessionProposal(ChangeSessionProposalStatusCommand command) {
        SessionProposal proposal = sessionProposalRepository.findById(command.proposalId())
                .orElseThrow(() -> new NotFoundException("SessionProposal not found: " + command.proposalId()));
        switch (command.newStatus()) {
            case ACCEPTED -> acceptAndAppendOutbox(proposal);
            case DECLINED -> proposal.decline();
            case WAITLISTED -> proposal.waitlist();
            case SUBMITTED -> throw new IllegalArgumentException(
                    "Cannot change a proposal back to SUBMITTED");
        }
        sessionProposalRepository.save(proposal);
        return SessionProposalMapper.toDTO(proposal);
    }

    private void acceptAndAppendOutbox(SessionProposal proposal) {
        proposal.accept();
        Presenter presenter = proposal.getPresenter();
        if (presenter == null) {
            throw new IllegalStateException(
                    "Accepted proposal " + proposal.getId() + " has no presenter");
        }
        Instant occurredAt = Instant.now();
        SessionProposalAcceptedEvent event = new SessionProposalAcceptedEvent(
                UUID.randomUUID(),
                occurredAt,
                SessionProposalAcceptedEvent.CURRENT_VERSION,
                proposal.getId(),
                proposal.getCfpId(),
                proposal.getTitle(),
                presenter.getId(),
                presenter.getFirstName(),
                presenter.getLastName(),
                presenter.getEmail().address());
        outboxRepository.append(event);
        Log.infof("Accepted proposal %s; appended outbox event %s (type %s)",
                proposal.getId(), event.eventId(), SessionProposalAcceptedEvent.EVENT_TYPE);
    }
}
