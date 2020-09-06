package com.ampaschal.entities;

import lombok.Getter;
import lombok.Setter;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Locale;

/**
 * @author Amusuo Paschal C.
 * @since 7/23/2020 9:17 PM
 */

@Getter
@Setter
@RequestScoped
public class RequestHeaderContext {

    private Locale locale;
    private String baseUrl;
    private MultivaluedMap<String, String> httpHeaders;
}
