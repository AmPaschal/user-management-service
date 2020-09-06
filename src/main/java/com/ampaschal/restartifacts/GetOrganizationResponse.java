package com.ampaschal.restartifacts;

import com.ampaschal.entities.OrganizationHeader;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Amusuo Paschal C.
 * @since 8/25/2020 12:05 PM
 */

@Getter
@Setter
public class GetOrganizationResponse extends BaseResponse {

    private static final long serialVersionUID = -3945078820391835653L;
    private OrganizationData organization;
    private List<OrganizationHeader> headers;
}
