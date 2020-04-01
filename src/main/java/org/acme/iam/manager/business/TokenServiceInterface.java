package org.acme.iam.manager.business;

import java.util.Base64;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.acme.iam.manager.dto.TokenData;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.FormParam;
import org.jboss.resteasy.annotations.jaxrs.HeaderParam;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("/auth")
@ClientHeaderParam(name = "Authorization", value = "{lookupAuth}")
@RegisterRestClient(configKey="token-api")
public interface TokenServiceInterface {

    @POST
    @Path("/realms/{realm}/protocol/{protocol}/token")
    @Produces(MediaType.APPLICATION_JSON)
    TokenData getToken(@PathParam final String realm,
                       @PathParam final String protocol, 
                       @FormParam("grant_type") String grantType,
                       @FormParam("username") String username,
                       @FormParam("password") String password,
                       @FormParam("client_id") String clientID,
                       @HeaderParam("Accept") String acceptHeader,
                       @HeaderParam("Content.Type") String contentType);

     default String lookupAuth() {
        return "Basic " + 
             Base64.getEncoder().encodeToString("app-authz-rest-springboot:secret".getBytes());
      }
}