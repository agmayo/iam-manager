
package org.acme.iam.manager.health;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.acme.iam.manager.business.UserService;
import org.acme.iam.manager.dto.IamUser;
import org.acme.iam.manager.dto.UserToken;
import org.acme.iam.manager.dto.UserTokenRequest;
import org.acme.iam.manager.service.TokenService;
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
    TokenService tokenService;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse
        .named("Connection health check");

        UserTokenRequest adminUser= new UserTokenRequest();
            adminUser.setPassword("Pa55w0rd");
            adminUser.setUsername("admin");
            String realm= "master";
            UserToken mytoken = tokenService.buildUserToken(adminUser.getUsername(),adminUser.getPassword(),realm);
              

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

}