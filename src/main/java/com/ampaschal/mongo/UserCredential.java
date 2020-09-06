package com.ampaschal.mongo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserCredential {

    public static final String PASSWORDS = "password";
    public static final String CHANGE_PASSWORD_ON_LOGIN = "changePasswordOnLogin";
    public static final String LAST_FAILED_LOGIN = "lastFailedLogin";
    public static final String FAILED_LOGIN_COUNT = "failedLoginCount";

	private String password;

	private boolean changePasswordOnLogin;

	private Date lastSuccessfulLogin;

	private Date lastFailedLogin;

	private Long failedLoginCount;
	
	private Date passwordUpdateTime;

}
