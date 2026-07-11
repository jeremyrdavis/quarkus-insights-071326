package io.arrogantprogrammer.quarkusinsights.cfp.infrastructure;

import io.arrogantprogrammer.quarkusinsights.cfp.application.CfpService;
import io.arrogantprogrammer.quarkusinsights.cfp.application.CreatePresenterCommand;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

@Path("/presenters")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PresenterResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PresenterResource.class);

    @Inject
    CfpService cfpService;

    @POST
    public Response createPresenter(@Valid PresenterParameters parameters) {
        LOGGER.debug("createPresenter: {}", parameters);
        CreatePresenterCommand createPresenterCommand = new CreatePresenterCommand(
                new EmailAddress(parameters.email()),
                parameters.firstName(),
                parameters.lastName()
        );
        var presenterDTO = cfpService.registerPresenter(createPresenterCommand);
        return Response.created(URI.create("/" + presenterDTO.emailAddress())).entity(presenterDTO).build();
    }

    @GET
    @Path("/{email}")
    public Response getPresenter(@PathParam("email") String email) {
        var submitterDTO = cfpService.getPresenter(email);
        if (submitterDTO == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().entity(submitterDTO).build();
    }

    @PUT
    @Path("/{email}")
    public Response updatePresenter(@PathParam("email") String email, @Valid PresenterParameters parameters) {
        LOGGER.debug("updatePresenter: {}, {}", email, parameters);
        var submitterDTO = cfpService.updatePresenter(email, parameters);
        return Response.ok().entity(submitterDTO).build();
    }

    @DELETE
    @Path("/{email}")
    public Response deletePresenter(@PathParam("email") String email) {
        cfpService.deletePresenter(email);
        return Response.noContent().build();
    }
}
