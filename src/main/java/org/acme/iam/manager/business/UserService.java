package org.acme.iam.manager.business;

import org.acme.iam.manager.dto.IamUser;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


@Path("/auth")
@RegisterRestClient(configKey="token-api")
public interface UserService {
    
    @GET
    @Path("/admin/realms/{realms}/users")
    @Produces("application/json")
    List<IamUser> getUser(@PathParam final String realms,
                    @QueryParam("username") String username,                  
                    @HeaderParam("Authorization") String authorizationHeader,
                    @HeaderParam("Content.Type") String contentType);


}