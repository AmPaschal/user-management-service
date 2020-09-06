package com.ampaschal.restartifacts;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SignUpForProductRequest extends BaseUserRequest{


    @NotBlank(message = "productId field must not be null or empty")
    private String productId;

    @NotBlank(message = "userId field must not be null or empty")
    private String userId;


}
