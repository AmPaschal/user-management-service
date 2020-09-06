package com.ampaschal.restartifacts;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author Amusuo Paschal C.
 * @since 8/17/2020 3:01 PM
 */

@Getter
@Setter
public class CreateOrganizationRequest extends BaseOrganizationRequest {

    // These admin fields, if set, would create a new admin user
    @NotBlank
    private String adminFirstName;

    @NotBlank
    private String adminLastName;

    @NotBlank
    private String adminEmail;

    @NotBlank
    private String adminPhoneNumber;

    // This is the user id of the admin creating the account
    @NotBlank
    private String superAdminUserId;


}
