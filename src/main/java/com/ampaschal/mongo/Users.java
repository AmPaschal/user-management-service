package com.ampaschal.mongo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Users extends BaseEntity {
    public static final String USER_CREDENTIAL = "credential";
    public static final String USER_EMAIL = "email";
    public static final String USER_PHONE_NUMBER = "phoneNumber";
    public static final String USER_LOCKED = "locked";

    private String userId;

    private String orgId;

	private boolean locked;

	private String lastName;

	private String firstName;

	private String middleName;

	private String title;
	
	private String email;
	
	private String gender;
	
	private String phoneNumber;

	private String portrait;
	
	private String username;

	private String nin;

	private String bvn;

	private Date dateOfBirth;

	private UserCredential credential;

	private List<String> roles;

	private Date lastModifiedDate;

	private boolean emailVerified;

	private Date emailVerifiedDate;

	private Map<String, Object> extraData;
	
}
