package org.acme.iam.manager.business;

import org.acme.iam.manager.dto.IamUser;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;


@Path("/auth")
@RegisterRestClient(configKey="token-api")
@RegisterProvider(value = RestClientExceptionMapper.class, priority = 50)
public interface UserService {
    
    @GET
    @Path("/admin/realms/{realms}/users")
    @Produces("application/json")
    List<IamUser> getUser(@PathParam final String realms,
                    @QueryParam("username") String username,                  
                    @HeaderParam("Authorization") String authorizationHeader,
                    @HeaderParam("Content.Type") String contentType);

    @POST
    @Path("/admin/realms/{realms}/users")
    @Produces("application/json")
    Response createUser(@PathParam final String realms,
                    @HeaderParam("Authorization") String authorizationHeader,
                    @HeaderParam("Content-Type") String contentType,
                    IamUser user);


}