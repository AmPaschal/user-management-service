package com.ampaschal.restartifacts;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Amusuo Paschal C.
 * @since 9/6/2020 4:37 PM
 */

@Getter
@Setter
public class GetUserListResponse extends BaseResponse {

    private List<UserData> users;
}
