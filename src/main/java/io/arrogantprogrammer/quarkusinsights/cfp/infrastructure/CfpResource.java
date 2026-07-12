package io.arrogantprogrammer.quarkusinsights.cfp.infrastructure;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceSessionFormat;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceTrack;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;
import io.arrogantprogrammer.quarkusinsights.cfp.application.CfpService;
import io.arrogantprogrammer.quarkusinsights.cfp.application.CreateCfpCommand;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.stream.Collectors;

@Path("/cfp")
public class CfpResource {

    @Inject
    CfpService cfpService;

    @POST
    public Response createCfp(@Valid CfpParameters parameters){
        Log.debugf("createCfp: {}", parameters);
        CreateCfpCommand createCfpCommand = new CreateCfpCommand(
                parameters.cfpOpens(),
                parameters.cfpCloses(),
                parameters.conferenceName(),
                parameters.conferenceUrl(),
                parameters.conferenceDescription(),
                parameters.formats(),
                parameters.conferenceTracks(),
                new EmailAddress(parameters.contactEmailAddress())
        );
        var cfpDTO = cfpService.createCfp(createCfpCommand);
        return Response.created(URI.create("/" + cfpDTO.id())).entity(cfpDTO).build();
    }
}
