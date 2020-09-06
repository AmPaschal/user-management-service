package com.ampaschal.entities;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Amusuo Paschal C.
 * @since 9/6/2020 4:58 PM
 */

@Getter
@Setter
public class GetUserListParam {

    private Integer pageSize;
    private Integer pageStart;
    private Boolean active;
    private String search;
}
