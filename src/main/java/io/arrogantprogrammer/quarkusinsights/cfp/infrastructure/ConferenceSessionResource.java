package io.arrogantprogrammer.quarkusinsights.cfp.infrastructure;

import jakarta.annotation.Resource;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/conference-sessions")
public class ConferenceSessionResource {

    @POST
    public Resource createConferenceSession(ConferenceSessionParameters parameters) {
        return null;
    }
}
