
package org.acme.iam.manager.health;

import java.util.Base64;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.acme.iam.manager.business.TokenServiceInterface;
import org.acme.iam.manager.business.UserService;
import org.acme.iam.manager.dto.IamUser;
import org.acme.iam.manager.dto.TokenData;
import org.acme.iam.manager.dto.UserToken;
import org.acme.iam.manager.dto.UserTokenRequest;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.inject.RestClient;


@Readiness
@ApplicationScoped
public class KeycloakConnectionHealthCheck implements HealthCheck {
    @Inject
    @RestClient
    UserService userService;
    @Inject
    @RestClient
    TokenServiceInterface tokenServiceInterface;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse
        .named("Connection health check");

        UserTokenRequest adminUser= new UserTokenRequest();
            adminUser.setPassword("Pa55w0rd");
            adminUser.setUsername("admin");
            String realm= "master";
            UserToken mytoken = buildAdminToken(adminUser.getUsername(),adminUser.getPassword(),realm);
              

    try {
        List<IamUser> userList = userService.getUser("master",
        "alice",
        "Bearer " + mytoken.getAccessToken(),
        MediaType.APPLICATION_FORM_URLENCODED);
        IamUser user= userList.get(0);
        responseBuilder.withData("username: ", user.getUsername()).up();
    } catch (IllegalStateException e) {
        responseBuilder.down();
    }

    return responseBuilder.build();


    }
  
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