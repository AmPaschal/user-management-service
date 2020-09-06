package com.ampaschal.resources;

import com.ampaschal.restartifacts.ActivateOrganizationRequest;
import com.ampaschal.restartifacts.CreateOrganizationResponse;
import com.ampaschal.restartifacts.GetOrganizationListRequest;
import com.ampaschal.restartifacts.GetOrganizationListResponse;
import com.ampaschal.restartifacts.GetOrganizationRequest;
import com.ampaschal.restartifacts.GetOrganizationResponse;
import com.ampaschal.restartifacts.SignUpOrganizationRequest;
import com.ampaschal.restartifacts.UpdateOrganizationRequest;
import com.ampaschal.services.OrganizationService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Amusuo Paschal C.
 * @since 8/17/2020 2:42 PM
 */

@Path("/organization")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrganizationResource {

    @Inject
    OrganizationService organizationService;

    @POST
    @Path("sign-up")
    public CreateOrganizationResponse signUpOrganization(@Valid SignUpOrganizationRequest request) {
        return organizationService.createOrganization(request);
    }

    @POST
    @Path("update")
    public CreateOrganizationResponse updateOrganization(@Valid UpdateOrganizationRequest request) {
        return organizationService.updateOrganization(request);
    }

    @GET
    @Path("list")
    public GetOrganizationListResponse getOrganizationList(@BeanParam GetOrganizationListRequest request) {
        return organizationService.getOrganizationList(request);
    }

    @GET
    @Path("find")
    public GetOrganizationResponse findOrganization(@BeanParam GetOrganizationRequest request) {
        return organizationService.findOrganization(request);
    }

    @POST
    @Path("activate")
    public GetOrganizationResponse activateOrganization(@Valid ActivateOrganizationRequest request) {

        return organizationService.activateOrganization(request);

    }

    @POST
    @Path("deactivate")
    public GetOrganizationResponse deactivateOrganization(@Valid ActivateOrganizationRequest request) {

        return organizationService.deactivateOrganization(request);

    }


}
