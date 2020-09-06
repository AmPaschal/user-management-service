package com.ampaschal.restartifacts;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Amusuo Paschal C.
 * @since 9/6/2020 3:23 PM
 */

@Getter
@Setter
public class LoginResponse extends BaseResponse {

    private static final long serialVersionUID = -1040784832283490774L;
    private UserData user;

    public LoginResponse() {
        super(ResponseCode.ERROR);
    }
}
