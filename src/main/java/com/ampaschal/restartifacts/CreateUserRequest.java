package com.ampaschal.restartifacts;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Amusuo Paschal C.
 * @since 8/3/2020 1:37 PM
 */

@Getter
@Setter
public class CreateUserRequest extends BaseUserRequest{

    private boolean partner;
    private boolean socialSignUp;
    private String orgId;

}
