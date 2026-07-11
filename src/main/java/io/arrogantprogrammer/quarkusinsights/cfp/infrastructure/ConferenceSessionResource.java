package io.arrogantprogrammer.quarkusinsights.cfp.infrastructure;

import io.arrogantprogrammer.quarkusinsights.cfp.application.CfpService;
import io.arrogantprogrammer.quarkusinsights.cfp.application.ConferenceSessionDTO;
import io.arrogantprogrammer.quarkusinsights.cfp.application.CreateConferenceSessionCommand;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/conference-sessions")
public class ConferenceSessionResource {

    @Inject
    CfpService cfpService;

    @POST
    public Response createConferenceSession(@Valid ConferenceSessionParameters parameters) {
        Log.debugf("createConferenceSession: {}", parameters);
        CreateConferenceSessionCommand createConferenceSessionCommand = new CreateConferenceSessionCommand(
                parameters.title(),
                parameters.description(),
                parameters.conferenceSessionFormat(),
                parameters.track(),
                parameters.level(),
                parameters.language(),
                new EmailAddress(parameters.presenterEmail()),
                parameters.presentationOutline(),
                parameters.programmingLanguagesUsed(),
                parameters.preRequisiteKnowledge());
        ConferenceSessionDTO conferenceSessionDTO = cfpService.createConferenceSession(createConferenceSessionCommand);
        return Response.status(Response.Status.CREATED).entity(conferenceSessionDTO).build();
    }
}
