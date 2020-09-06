package com.ampaschal.restartifacts;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Amusuo Paschal C.
 * @since 8/19/2020 5:49 PM
 */

@Getter
@Setter
public class OrganizationData implements Serializable {

    private static final long serialVersionUID = -2605438707961669539L;
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
    private boolean activationStatus;
    private List<String> products;
    private Map<String, Object> extraData;
    private String adminEmail;
    private String adminName;
    private String adminPhoneNumber;
    private String superAdminEmail;
    private String dateCreated;
}
