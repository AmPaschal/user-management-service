package com.ampaschal.restartifacts;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author Amusuo Paschal C.
 * @since 8/26/2020 4:48 PM
 */

@Getter
@Setter
public class ActivateOrganizationRequest {

    @NotBlank
    private String orgId;

    @NotBlank
    private String adminEmail;
}
