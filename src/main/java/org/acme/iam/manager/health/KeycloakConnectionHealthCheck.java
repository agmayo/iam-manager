
package org.acme.iam.manager.health;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.acme.iam.manager.service.UserService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;


@Readiness
@ApplicationScoped
public class KeycloakConnectionHealthCheck implements HealthCheck {
    @ConfigProperty(name = "user.for.healtcheck")
    String username;
 
    @Inject
    UserService userService;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse
        .named("Connection health check");
               
    try {
        boolean userExists = userService.checkUserExistence(username);
        responseBuilder.withData("userExists: ", userExists).up();
    } catch (IllegalStateException e) {
        responseBuilder.down();
    }

    return responseBuilder.build();


    }

}