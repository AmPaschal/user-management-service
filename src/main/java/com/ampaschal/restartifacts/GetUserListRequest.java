package com.ampaschal.restartifacts;

import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.QueryParam;

/**
 * @author Amusuo Paschal C.
 * @since 9/6/2020 4:38 PM
 */

@Getter
@Setter
public class GetUserListRequest {

    @QueryParam("pageSize")
    private Integer pageSize;

    @QueryParam("pageNo")
    private Integer pageNo;

    @QueryParam("active")
    private Boolean active;

    @QueryParam("search")
    private String search;
}
