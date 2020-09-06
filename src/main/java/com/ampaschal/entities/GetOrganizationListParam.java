package com.ampaschal.entities;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Amusuo Paschal C.
 * @since 8/25/2020 1:50 PM
 */

@Getter
@Setter
public class GetOrganizationListParam {

    private Integer pageSize;
    private Integer pageStart;
    private Boolean active;
    private String search;
}
