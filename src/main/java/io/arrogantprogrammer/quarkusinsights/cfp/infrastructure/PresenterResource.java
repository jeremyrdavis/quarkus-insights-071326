package io.arrogantprogrammer.quarkusinsights.cfp.infrastructure;

import io.arrogantprogrammer.quarkusinsights.cfp.application.CfpService;
import io.arrogantprogrammer.quarkusinsights.cfp.application.CreatePresenterCommand;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

@Path("/presenters")
public class PresenterResource {

    @Inject
    CfpService cfpService;

    @POST
    public Response createPresenter(PresenterParameters parameters) {
        CreatePresenterCommand createPresenterCommand = new CreatePresenterCommand(
                new EmailAddress(parameters.email()),
                parameters.firstName(),
                parameters.lastName()
        );
        var submitterDTO = cfpService.registerPresenter(createPresenterCommand);
        return Response.ok().entity(submitterDTO).build();
    }

    @GET
    @Path("/{email}")
    public Response getPresenter(@PathParam("email") String email) {
        var submitterDTO = cfpService.getPresenter(email);
        return Response.ok().entity(submitterDTO).build();
    }

    @PUT
    @Path("/{email}")
    public Response updatePresenter(@PathParam("email") String email, PresenterParameters parameters) {
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
