package com.ampaschal.restartifacts;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author Amusuo Paschal C.
 * @since 8/18/2020 10:13 AM
 */

@Getter
@Setter
public class UpdateOrganizationRequest extends BaseOrganizationRequest {

    @NotBlank
    private String orgId;
}
