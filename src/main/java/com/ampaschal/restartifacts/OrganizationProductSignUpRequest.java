package com.ampaschal.restartifacts;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author Amusuo Paschal C.
 * @since 8/18/2020 11:44 AM
 */

@Getter
@Setter
public class OrganizationProductSignUpRequest {
    @NotBlank
    private String productId;

    @NotBlank
    private String orgId;
}
