package com.ampaschal.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author Amusuo Paschal C.
 * @since 7/21/2020 8:09 PM
 */

@Getter
@Setter
public class UserCreationActivityData {

    private String firstName;
    private String lastName;
    private String email;
    private Date activityDate;
    private String activityPerformed;
    private String ipAddress;
    private String deviceType;


}
