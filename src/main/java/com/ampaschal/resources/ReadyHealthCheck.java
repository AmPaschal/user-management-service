package com.ampaschal.resources;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

/**
 * @author Amusuo Paschal C.
 * @since 7/25/2020 8:53 PM
 */

@Readiness
public class ReadyHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("User Management Service is ready to process your requests");
    }
}
