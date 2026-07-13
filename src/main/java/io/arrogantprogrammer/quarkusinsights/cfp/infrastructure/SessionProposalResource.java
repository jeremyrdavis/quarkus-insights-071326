package io.arrogantprogrammer.quarkusinsights.cfp.infrastructure;

import io.arrogantprogrammer.quarkusinsights.cfp.application.CfpApplicationService;
import io.arrogantprogrammer.quarkusinsights.cfp.application.CreateSessionProposalCommand;
import io.arrogantprogrammer.quarkusinsights.cfp.application.ReviewSessionProposalCommand;
import io.arrogantprogrammer.quarkusinsights.cfp.application.SessionProposalDTO;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Path("/session-proposals")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
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

    @GET
    public List<SessionProposalDTO> getSessionProposals(@QueryParam("cfpId") UUID cfpId) {
        if (cfpId == null) {
            throw new BadRequestException("cfpId query parameter is required");
        }
        return cfpService.getSessionProposalsForCfp(cfpId);
    }

    @PUT
    @Path("/{id}/status")
    public SessionProposalDTO reviewSessionProposal(
            @PathParam("id") UUID id,
            @Valid ReviewSessionProposalParameters params) {
        return cfpService.reviewSessionProposal(new ReviewSessionProposalCommand(id, params.status));
    }
}
