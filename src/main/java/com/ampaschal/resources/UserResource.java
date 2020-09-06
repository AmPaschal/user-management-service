package com.ampaschal.resources;

import com.ampaschal.restartifacts.CreateUserRequest;
import com.ampaschal.restartifacts.CreateUserResponse;
import com.ampaschal.restartifacts.GetUserListRequest;
import com.ampaschal.restartifacts.GetUserListResponse;
import com.ampaschal.restartifacts.LoginRequest;
import com.ampaschal.restartifacts.LoginResponse;
import com.ampaschal.restartifacts.UpdateUserRequest;
import com.ampaschal.services.UserService;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Amusuo Paschal
 * @since 20 July 2020, 15:16:47
 */

@Slf4j
@Path("/users")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    private UserService userService;

    @POST
    @Path("/sign-up")
    public CreateUserResponse signUp(@Valid CreateUserRequest request) {

        try {
            return userService.createUser(request);
        } catch (Exception ex) {
            log.error("Error creating user", ex);
            return new CreateUserResponse();
        }

    }

    @POST
    @Path("/update")
    public CreateUserResponse updateUser(@Valid UpdateUserRequest request) {
        try {
            return userService.updateUser(request);
        } catch (Exception ex) {
            log.error("Error updating user", ex);
            return new CreateUserResponse();
        }
    }

    @GET
    @Path("/verify-email")
    public Response verifyEmail(@QueryParam("token") @NotNull String token) {
        try {
            return userService.verifyEmail(token);
        } catch (Exception ex) {
            log.error("Error verifying email", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/get")
    public GetUserListResponse getUsers(@Valid GetUserListRequest request) {
        try {
            return userService.getUserList(request);
        } catch (Exception ex) {
            log.error("Error verifying email", ex);
            return new GetUserListResponse();
        }
    }

    @POST
    @Path("/login")
    public LoginResponse loginUser(@Valid LoginRequest loginRequest){
        try {
            return userService.loginUser(loginRequest);
        } catch (Exception ex) {
            log.error("Error creating user", ex);
            return new LoginResponse();
        }
    }

    @POST
    @Path("/unlock")
    public LoginResponse unlockUser(@QueryParam("userId") String userId) {
        try {
            return userService.unlockUser(userId);
        } catch (Exception ex) {
            log.error("Error creating user", ex);
            return new LoginResponse();
        }
    }
}
