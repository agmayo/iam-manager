package org.acme.iam.manager.events;

import java.util.Base64;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.acme.iam.manager.dto.UserToken;
import org.acme.iam.manager.dto.UserTokenRequest;
import org.acme.iam.manager.exceptions.BasicAuthHeaderParsingException;
import org.acme.iam.manager.restclient.TokenRestClientInterface;
import org.acme.iam.manager.service.TokenService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@Path("/token")
public class LoginResource {

    // Logs to be sent to Logstash
    private static final Logger LOG = Logger.getLogger(LoginResource.class);
    @ConfigProperty(name = "master.realm")
    String realm;

    @Inject
    @RestClient
    TokenRestClientInterface tokenServiceInterface;

    @Inject
    TokenService tokenService;

    // Metrics to be sent to Prometheus
    @Path("/raw")
    @POST
    @Counted(name = "rawLoginCalls", description = "How many times the /login/raw resource has been called")
    @Timed(name = "rawLoginTime", description = "A measure of how long it takes to retrieve a token in json format.", unit = MetricUnits.MILLISECONDS)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginRaw(UserTokenRequest userData) {
     
        try{
            UserToken userToken = tokenService.buildUserToken(userData.getUsername(), userData.getPassword(), realm);
            return buildOkTokenResponse(userToken, userData, "raw");
        }catch(WebApplicationException wae){
            return buildKoTokenResponse(userData);
        }
    }

    // Metrics to be sent to Prometheus
    @GET
    @Path("/cookie")
    @Counted(name = "cookieLoginCalls", description = "How many times the /login/cookie resource has been called")
    @Timed(name = "cookieLoginTime", description = "A measure of how long it takes to retrieve a token in cookie format.", unit = MetricUnits.MILLISECONDS)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginCookie(@HeaderParam("authorization") String basicAuthHeader) {
        UserTokenRequest userData = new UserTokenRequest();
 
        try{
            
            userData = parseBasicAuth(basicAuthHeader);
            UserToken userToken = tokenService.buildUserToken(userData.getUsername(), userData.getPassword(), realm);
            return buildOkTokenResponse(userToken, userData, "cookie");
        }catch(WebApplicationException wae){
            return buildKoTokenResponse(userData);
        }catch(BasicAuthHeaderParsingException bape){
            LOG.warn(bape.getLocalizedMessage());
            LOG.info("Returning: "+ Response.Status.BAD_REQUEST);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    private UserTokenRequest parseBasicAuth(String rawHeader) throws BasicAuthHeaderParsingException{

        try{
            String decodedCreds = "";
            // Header is in the format "Basic 5tyc0uiDat4"
            String[] rawHeaderParts = rawHeader.split("\\s+");
            // We save "5tyc0uiDat4" and ommit "Basic"
            String encodedCreds = rawHeaderParts[1];
            // Decode the data back to original string
            byte[] bytes = null;
            try {
                bytes = Base64.getDecoder().decode(encodedCreds);
            } catch (IllegalArgumentException iae) {
                LOG.error("Authorization Header does not contain a valid Base64: " + encodedCreds);
            }
            // We get the string "user:password"
            decodedCreds = new String(bytes);
            String[] creds = decodedCreds.split(":");
            return new UserTokenRequest(creds[0],creds[1]);

        } catch(Exception ex){
            LOG.warn(ex.getLocalizedMessage());
            throw new BasicAuthHeaderParsingException();
        }
    }
    private Response buildOkTokenResponse(UserToken userToken, UserTokenRequest userData, String mode){
        LOG.info("Returning token " +  
                 "User: " + userData.getUsername() + 
                 " Access Token: " + userToken.getAccessToken() +
                 " Refresh Token: " + userToken.getRefreshToken());
        
        if (mode.contentEquals("raw"))
            return Response.ok(userToken).build();
        else if(mode.contentEquals("cookie")){
            String[] tokenParts = userToken.getAccessToken().split("[.]");
            String tokenHeader = tokenParts[0];
            String tokenBody = tokenParts[1];
            String tokenSignature = tokenParts[2];
            NewCookie payloadCookie = new NewCookie("payload", 
                                                    tokenHeader + "." + tokenBody,
                                                    "/",
                                                    "localhost",
                                                    NewCookie.DEFAULT_VERSION,
                                                    null,
                                                    //set expiration for the cookie as token -1 to avoid complicated situations.
                                                    userToken.getAccessTokenExpiration()-1,
                                                    null,
                                                    false,
                                                    false);
            LOG.debug("Token payload cookie created: " + payloadCookie.toString());
            NewCookie signatureCookie = new NewCookie("signature",
                                                       tokenSignature,
                                                       "/",
                                                       "localhost",
                                                       NewCookie.DEFAULT_VERSION,
                                                       null,
                                                       //set expiration for the cookie as token -1 to avoid complicated situations.
                                                       userToken.getAccessTokenExpiration()-1,
                                                       null,
                                                       false,
                                                       true);
            LOG.debug("Token signature cookie created: " + signatureCookie.toString());
            return Response.noContent().cookie(payloadCookie, signatureCookie).build();

        }else{
            LOG.error("Unknown mode for buildOkTokenResponse(): " + mode);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    private Response buildKoTokenResponse(UserTokenRequest userData){
        LOG.info("No token could be obtained"+  "User:" + userData.getUsername());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

}