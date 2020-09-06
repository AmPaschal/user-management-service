package com.ampaschal.restartifacts;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Amusuo Paschal C.
 * @since 8/17/2020 3:01 PM
 */

@Getter
@Setter
public class CreateOrganizationResponse extends BaseResponse{

    private OrganizationData organization;
}
