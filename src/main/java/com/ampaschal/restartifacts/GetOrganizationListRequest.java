package com.ampaschal.restartifacts;

import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.QueryParam;

/**
 * @author Amusuo Paschal C.
 * @since 8/25/2020 10:31 AM
 */

@Getter
@Setter
public class GetOrganizationListRequest {

    @QueryParam("pageSize")
    private Integer pageSize;

    @QueryParam("pageNo")
    private Integer pageNo;

    @QueryParam("active")
    private Boolean active;

    @QueryParam("search")
    private String search;
}
