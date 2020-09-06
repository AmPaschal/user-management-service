package com.ampaschal.restartifacts;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author Amusuo Paschal C.
 * @since 7/21/2020 4:19 PM
 */

@Getter
@Setter
public class UpdateUserRequest extends BaseUserRequest {

    @NotBlank(message = "userId field must not be null or empty")
    private String userId;



}
