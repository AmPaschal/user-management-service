package com.ampaschal.resources;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

/**
 * @author Amusuo Paschal C.
 * @since 7/25/2020 8:51 PM
 */

@Liveness
public class LiveHealthCheck implements HealthCheck {
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("User Management Service is Live");
    }
}
