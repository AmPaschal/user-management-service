package com.ampaschal.resources;

import com.ampaschal.entities.RequestHeaderContext;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.util.Locale;

/**
 * @author Amusuo Paschal C.
 * @since 7/23/2020 9:12 PM
 */

@Slf4j
@Provider
public class RequestFilter implements ContainerRequestFilter {

    @Inject
    RequestHeaderContext headerContext;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) {

        Locale locale = containerRequestContext.getAcceptableLanguages() != null && !containerRequestContext.getAcceptableLanguages().isEmpty()
                ? containerRequestContext.getAcceptableLanguages().get(0) : null;

        log.debug("Request intercepted: {} and Locale is {}", containerRequestContext.getUriInfo().getBaseUri(), locale);


        headerContext.setLocale(locale);
        headerContext.setBaseUrl(containerRequestContext.getUriInfo().getBaseUri().toString());
        headerContext.setHttpHeaders(containerRequestContext.getHeaders());

    }
}
