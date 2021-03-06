package org.acme.iam.manager.health;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@ApplicationScoped
public class BasicHealthCheck implements HealthCheck{

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("Basic Health Check");
    }

}