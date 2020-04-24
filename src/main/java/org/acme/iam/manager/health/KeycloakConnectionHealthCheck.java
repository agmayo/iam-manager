
package org.acme.iam.manager.health;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.acme.iam.manager.dto.IamUser;
import org.acme.iam.manager.service.UserService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;


@Readiness
@ApplicationScoped
public class KeycloakConnectionHealthCheck implements HealthCheck {
    @ConfigProperty(name = "alice.username")
    String username;
 
    @Inject
    UserService userService;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse
        .named("Connection health check");
               
    try {
        List<IamUser> userList= userService.checkUserExistence(username);
        IamUser user= userList.get(0);
        responseBuilder.withData("username: ", user.getUsername()).up();
    } catch (IllegalStateException e) {
        responseBuilder.down();
    }

    return responseBuilder.build();


    }

}