package com.ampaschal.restartifacts;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author Amusuo Paschal C.
 * @since 9/2/2020 2:48 PM
 */

@Getter
@Setter
public class SignUpOrganizationRequest extends BaseOrganizationRequest {

    // Admin user, if set, would be used as the admin user
    @NotBlank
    private String adminUserId;
}
