package com.ampaschal.restartifacts;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Amusuo Paschal
 * @since 20 July 2020, 15:16:47
 */

@Getter
@Setter
public class BaseResponse implements Serializable {

    private static final long serialVersionUID = -2873275190346296143L;
    private int code;
    private String description;

    public BaseResponse() {
        this(ResponseCode.ERROR);
    }

    public BaseResponse(ResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.description = responseCode.getDescription();
    }

    public void assignResponseCode(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
