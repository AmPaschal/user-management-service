package com.ampaschal.restartifacts;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author Amusuo Paschal C.
 * @since 8/3/2020 4:24 PM
 */

@Getter
@Setter
public class UserData {

    private String userId;
    private String email;
    private String firstName;
    private String password;
    private String phoneNumber;
    private String lastName;
    private String middleName;
    private String gender;
    private String username;
    private String title;
    private String nin;
    private String bvn;
    private String dateOfBirth;
    private String orgId;
    private List<String> roles;
    private Map<String, Object> extraData;
}
