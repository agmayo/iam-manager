package org.acme.iam.manager.events;

import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.acme.iam.manager.service.TokenService;
import org.acme.iam.manager.service.UserService;
import org.acme.iam.manager.dto.IamUser;
import org.acme.iam.manager.dto.UserRegisterRequest;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("/user")
public class UserResource {

    // Logs to be sent to Logstash
    private static final Logger LOG = Logger.getLogger(UserResource.class);


    @Inject
    TokenService tokenService;
    @Inject
    UserService userService;



    @GET
    @Counted(name = "existsCalls", description = "How many times the /register/exists resource has been called")
    @Timed(name = "existsTime", description = "A measure of how long it takes to retrieve a person.", unit = MetricUnits.MILLISECONDS)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/exists/{username}")
    public Response checkUserExistence(@PathParam String username) {
        try{
            List<IamUser> userList= userService.checkUserExistence(username);
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
            Response response=userService.createUser(userData);
            LOG.info("User:" + userData.getUsername()+ " was correctly created");
            return Response.noContent().build();

        }catch(WebApplicationException wae){
            LOG.info( "User:" + userData.getUsername()+ " was not correctly created" );
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        
    }     

}