package io.arrogantprogrammer.quarkusinsights.cfp.infrastructure;

import io.arrogantprogrammer.quarkusinsights.cfp.application.CfpApplicationService;
import io.arrogantprogrammer.quarkusinsights.cfp.application.CreateSessionProposalCommand;
import io.arrogantprogrammer.quarkusinsights.cfp.application.SessionProposalDTO;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/session-proposals")
public class SessionProposalResource {

    @Inject
    CfpApplicationService cfpService;

    @POST
    public Response createSessionProposal(@Valid SessionProposalParameters parameters) {
        Log.debugf("createSessionProposal: {}", parameters);
        CreateSessionProposalCommand createSessionProposalCommand = new CreateSessionProposalCommand(
                parameters.cfpId(),
                parameters.title(),
                parameters.description(),
                parameters.conferenceSessionFormat(),
                parameters.conferenceTrack(),
                parameters.level(),
                parameters.language(),
                new EmailAddress(parameters.presenterEmail()),
                parameters.presentationOutline(),
                parameters.programmingLanguagesUsed(),
                parameters.preRequisiteKnowledge());
        SessionProposalDTO sessionProposalDTO = cfpService.createSessionProposal(createSessionProposalCommand);
        return Response.status(Response.Status.CREATED).entity(sessionProposalDTO).build();
    }
}
