package com.ampaschal.restartifacts;

import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.QueryParam;

/**
 * @author Amusuo Paschal C.
 * @since 8/25/2020 11:43 AM
 */

@Getter
@Setter
public class GetOrganizationRequest {

    @QueryParam("orgId")
    private String orgId;

    @QueryParam("name")
    private String name;

    @QueryParam("rcNumber")
    private String rcNumber;

    @QueryParam("adminEmail")
    private String adminEmail;
}
