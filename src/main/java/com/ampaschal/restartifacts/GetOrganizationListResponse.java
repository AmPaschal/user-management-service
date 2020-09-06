package com.ampaschal.restartifacts;

import com.ampaschal.entities.OrganizationHeader;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Amusuo Paschal C.
 * @since 8/20/2020 3:35 PM
 */

@Getter
@Setter
public class GetOrganizationListResponse extends BaseResponse {

    private static final long serialVersionUID = -975003352947136346L;

    private List<OrganizationData> organizations;
    private List<OrganizationHeader> headers;
    private OrganizationMetaData metaData;
}
