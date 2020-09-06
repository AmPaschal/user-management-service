package com.ampaschal.restartifacts;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateUserResponse extends BaseResponse {

    private UserData user;

    public CreateUserResponse() {
        super(ResponseCode.ERROR);
    }


    public CreateUserResponse(ResponseCode responseCode) {
        super(responseCode);
    }
}
