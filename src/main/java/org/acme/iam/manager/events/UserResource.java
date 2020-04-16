package org.acme.iam.manager.events;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.Email;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.acme.iam.manager.restclient.TokenRestClientInterface;
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
    private static final Logger LOG = Logger.getLogger(UserResource.class);

    @Inject
    @RestClient
    UserService userService;
    
    @Inject
    @RestClient
    TokenRestClientInterface tokenServiceInterface;

    @Inject
    Validator validator;


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
            String realm= "master";
            UserToken mytoken = buildAdminToken(adminUser.getUsername(),adminUser.getPassword(),realm);
            
            List<IamUser> userList = userService.getUser(realm,
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
            //
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
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/register")
    public Response createUser(@Valid UserRegisterRequest userData) {

        try{
            String username= userData.getUsername();
            String email= userData.getEmail();
            List<Credential> credentials=userData.getCredentials();
            Credential credential= credentials.get(0);
            String type= credential.getType();
            String value= credential.getValue();
            boolean temporary= credential.isTemporary();
            boolean enabled =userData.isEnabled();

            Set<ConstraintViolation<UserRegisterRequest>> violations = validator.validate(userData);

            UserTokenRequest adminUser= new UserTokenRequest();
            adminUser.setPassword("Pa55w0rd");
            adminUser.setUsername("admin");
            String realm ="master";
            UserToken mytoken = buildAdminToken(adminUser.getUsername(),adminUser.getPassword(),realm);
    
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
            user.setEnabled(enabled);

            Response response = userService.createUser(realm,
            "Bearer " + mytoken.getAccessToken(),
            MediaType.APPLICATION_FORM_URLENCODED,
            user);

            LOG.info("User:" + userData.getUsername()+ " was correctly created");
            return Response.noContent().build();

        }catch(WebApplicationException wae){
            LOG.info( "User:" + userData.getUsername()+ " was not correctly created" );
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        
    }    
     
    //TODO: This method should be in aggregator but we have injection problems plz help
    private UserToken buildAdminToken(String user, String password, String realm) throws WebApplicationException{
        String authorizationHeader = "Basic " + Base64.getEncoder().encodeToString("app-authz-rest-springboot:secret".getBytes());
        TokenData iamTokenInfo = tokenServiceInterface.getToken(realm,
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