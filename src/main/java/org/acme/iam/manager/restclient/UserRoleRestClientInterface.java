package org.acme.iam.manager.restclient;

import org.acme.iam.manager.dto.RealmMappings;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.HeaderParam;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import java.util.List;

import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;


@Path("/auth")
@RegisterRestClient(configKey="token-api")
@RegisterProvider(value = RestClientExceptionMapper.class, priority = 50)
public interface UserRoleRestClientInterface {

    @GET
    @Path("/admin/realms/{realms}/users/{user_id}/role-mappings/realm")
    @Produces("application/json")
    List<RealmMappings> getUserRoles(@PathParam final String realms,
                    @PathParam final String user_id,             
                    @HeaderParam("Authorization") String authorizationHeader,
                    @HeaderParam("Content.Type") String contentType);


}