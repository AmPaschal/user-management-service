package com.ampaschal.mongo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PasswordResetToken extends BaseEntity{

    public static final String PASSWORD_RESET_TOKEN = "token";

    private String token;

    private Date expiryDate;

    private String userId;
}
