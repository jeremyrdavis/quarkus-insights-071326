package io.arrogantprogrammer.quarkusinsights.cfp.infrastructure;

import io.arrogantprogrammer.quarkusinsights.cfp.application.CfpService;
import io.arrogantprogrammer.quarkusinsights.cfp.application.CreateCfpCommand;
import io.arrogantprogrammer.quarkusinsights.cfp.application.UpdateCfpCommand;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Path("/cfp")
public class CfpResource {

    @Inject
    CfpService cfpService;

    @POST
    public Response createCfp(@Valid CfpParameters parameters) {
        Log.debugf("createCfp: {}", parameters);
        CreateCfpCommand createCfpCommand = new CreateCfpCommand(
                parameters.cfpId(),
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
        return Response.created(URI.create("/cfp/" + cfpDTO.id())).entity(cfpDTO).build();
    }

    @GET
    public List<?> getAllCfps() {
        return cfpService.getAllCfps();
    }

    @GET
    @Path("/{id}")
    public Response getCfp(@PathParam("id") UUID id) {
        return cfpService.getCfp(id)
                .map(dto -> Response.ok(dto).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @PUT
    @Path("/{id}")
    public Response updateCfp(@PathParam("id") UUID id, @Valid CfpParameters parameters) {
        Log.debugf("updateCfp: {}", id);
        UpdateCfpCommand command = new UpdateCfpCommand(
                parameters.cfpOpens(),
                parameters.cfpCloses(),
                parameters.conferenceName(),
                parameters.conferenceUrl(),
                parameters.conferenceDescription(),
                parameters.formats(),
                parameters.conferenceTracks(),
                new EmailAddress(parameters.contactEmailAddress())
        );
        return cfpService.updateCfp(id, command)
                .map(dto -> Response.ok(dto).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCfp(@PathParam("id") UUID id) {
        boolean deleted = cfpService.deleteCfp(id);
        return deleted
                ? Response.noContent().build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }
}
