package org.acme.iam.manager.events;

import org.acme.iam.manager.business.Aggregator;
import org.acme.iam.manager.dto.IamUser;
import org.acme.iam.manager.dto.TokenData;
import org.acme.iam.manager.dto.UserToken;
import org.acme.iam.manager.dto.UserTokenRequest;
import org.acme.iam.manager.business.TokenServiceInterface;
import org.acme.iam.manager.business.UserService;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import java.util.Base64;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
public class UserResource {

    // Logs to be sent to Logstash
    private static final Logger LOG = Logger.getLogger(LoginResource.class);

    @Inject
    @RestClient
    UserService userService;
    
    @Inject
    @RestClient
    TokenServiceInterface tokenServiceInterface;


    @GET
    @Counted(name = "existsCalls", description = "How many times the /register/exists resource has been called")
    @Timed(name = "existsTime", description = "A measure of how long it takes to retrieve a person.", unit = MetricUnits.MILLISECONDS)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/exists/{username}")
    public Response checkUserExistence(@PathParam String username) {
      
        UserTokenRequest adminUser= new UserTokenRequest();
        adminUser.setPassword("Pa55w0rd");
        adminUser.setUsername("admin");
        UserToken mytoken = buildAdminToken(adminUser.getUsername(),adminUser.getPassword());
        
        List<IamUser> userList = userService.getUser("master",
        username,
        "Bearer " + mytoken.getAccessToken(),
        MediaType.APPLICATION_FORM_URLENCODED);
        if(userList.size()==0){
            LOG.info("User could not be found, returning 404");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        else{
            IamUser user= userList.get(0);
            String usernameIam = user.getUsername();
    
            if(usernameIam.compareTo(username)==0){
                LOG.info("User exists, returning 204");
                return Response.noContent().build();
            }
            else{
                LOG.info("User exists, but "+usernameIam+ " is not equal to: "+username);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

            }
        }

    }
    //TODO: This method should be in aggregator but we have injection problems plz help
    private UserToken buildAdminToken(String user, String password) {
        String authorizationHeader = "Basic " + Base64.getEncoder().encodeToString("app-authz-rest-springboot:secret".getBytes());
        TokenData iamTokenInfo = tokenServiceInterface.getToken("master",
        "openid-connect",
        "password",
        user,
        password,
        "admin-cli",
        MediaType.APPLICATION_JSON,
        MediaType.APPLICATION_FORM_URLENCODED,
        authorizationHeader);
        UserToken tokenData = new UserToken();
        tokenData.setAccessToken(iamTokenInfo.getAccessToken());
        tokenData.setRefreshToken(iamTokenInfo.getRefreshToken());
        tokenData.setAccessTokenExpiration(iamTokenInfo.getExpiresIn());
        tokenData.setRefreshTokenExpiration(iamTokenInfo.getRefreshExpiration());
        return tokenData;
    }

}