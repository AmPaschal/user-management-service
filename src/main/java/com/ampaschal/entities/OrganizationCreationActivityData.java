package com.ampaschal.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author Amusuo Paschal C.
 * @since 8/19/2020 4:04 PM
 */

@Getter
@Setter
public class OrganizationCreationActivityData {
    private String name;
    private String rcNumber;
    private Date activityDate;
    private String activityPerformed;
    private String ipAddress;
    private String deviceType;

}
