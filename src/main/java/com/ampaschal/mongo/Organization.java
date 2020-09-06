package com.ampaschal.mongo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author Amusuo Paschal C.
 * @since 8/17/2020 3:01 PM
 */

@Getter
@Setter
public class Organization extends BaseEntity {

    private String name;
    private String orgId;
    private String rcNumber;
    private boolean rcNumberVerificationStatus;
    private String website;
    private String industry;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String adminUserId;
    private String superAdminUserId;
    private boolean partner;
    private List<String> products;
    private Map<String, Object> extraData;
}
