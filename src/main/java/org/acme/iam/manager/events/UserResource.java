package org.acme.iam.manager.events;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.acme.iam.manager.business.TokenServiceInterface;
import org.acme.iam.manager.business.UserService;
import org.acme.iam.manager.dto.Credential;
import org.acme.iam.manager.dto.IamUser;
import org.acme.iam.manager.dto.TokenData;
import org.acme.iam.manager.dto.UserRegisterRequest;
import org.acme.iam.manager.dto.UserToken;
import org.acme.iam.manager.dto.UserTokenRequest;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

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
        try{
            UserTokenRequest adminUser= new UserTokenRequest();
            adminUser.setPassword("Pa55w0rd");
            adminUser.setUsername("admin");
            UserToken mytoken = buildAdminToken(adminUser.getUsername(),adminUser.getPassword());
            
            List<IamUser> userList = userService.getUser("master",
            username,
            "Bearer " + mytoken.getAccessToken(),
            MediaType.APPLICATION_FORM_URLENCODED);

            IamUser user= userList.get(0);
            String usernameIam = user.getUsername();

            if(userList.size()==0){
                LOG.info("User could not be found, returning 404");
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            else if(usernameIam.compareTo(username)==0){
                LOG.info("User exists, returning 204");
                return Response.noContent().build();
            }
            //amparo
            else{
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

            }
                      
        }catch(WebApplicationException wae){
            LOG.info("User exists, but is not equal to: "+username);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }  
        
    }

    @POST
    @Counted(name = "registerCalls", description = "How many times the /register resource has been called")
    @Timed(name = "registerTime", description = "A measure of how long it takes to retrieve a person.", unit = MetricUnits.MILLISECONDS)
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/register")
    public Response createUserR(UserRegisterRequest userData) {
        String username= userData.getUsername();
        String email= userData.getEmail();
        List<Credential> credentials=userData.getCredentials();
        Credential credential= credentials.get(0);
        String type= credential.getType();
        String value= credential.getValue();
        boolean temporary= credential.isTemporary();

        UserTokenRequest adminUser= new UserTokenRequest();
        adminUser.setPassword("Pa55w0rd");
        adminUser.setUsername("admin");
        UserToken mytoken = buildAdminToken(adminUser.getUsername(),adminUser.getPassword());

        Credential cred = new Credential();
        IamUser user = new IamUser();
        cred.setTemporary(temporary);
        cred.setType(type);
        cred.setValue(value);
        ArrayList<Credential> creds= new ArrayList<Credential>();
        creds.add(cred);
        user.setUsername(username);
        user.setEmail(email);
        user.setCredentials(creds);

        Response response = userService.createUser("master",
        "Bearer " + mytoken.getAccessToken(),
        MediaType.APPLICATION_FORM_URLENCODED,
        user);
        
        LOG.debug("Response from IAM: "+response.toString());
        return response;

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