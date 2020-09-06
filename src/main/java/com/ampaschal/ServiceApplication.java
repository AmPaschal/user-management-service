package com.ampaschal;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

import javax.ws.rs.core.Application;

/**
 * @author Amusuo Paschal C.
 * @since 7/24/2020 11:58 AM
 */

@OpenAPIDefinition(
        info = @Info(
                title = "User Management Service Documentations",
                version = "1.0.0"
        )
)
public class ServiceApplication extends Application {
}
