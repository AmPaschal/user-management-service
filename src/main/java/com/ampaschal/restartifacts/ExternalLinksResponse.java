package com.ampaschal.restartifacts;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author Amusuo Paschal C.
 * @since 8/28/2020 11:11 AM
 */

@Getter
@Setter
@ToString
public class ExternalLinksResponse extends BaseResponse {

    private static final long serialVersionUID = 3121235467591012493L;
    private List<String> productRoles;
}
