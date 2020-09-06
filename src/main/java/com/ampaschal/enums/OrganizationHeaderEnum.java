package com.ampaschal.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Amusuo Paschal C.
 * @since 8/25/2020 4:15 PM
 */

@Getter
public enum OrganizationHeaderEnum {

    NAME("name", "Organisation Name", true),
    RC_NUMBER("rcNumber", "RC Number", true),
    RC_NUMBER_VERIFICATION_STATUS("rcNumberVerificationStatus", "RC Number Verification Status", false),
    ACTIVATION_STATUS("activationStatus", "Active", false),
    WEBSITE("website", "Website", false),
    INDUSTRY("industry", "Industry", true),
    ADDRESS("address", "Address", false),
    COUNTRY("country", "Country", false),
    STATE("state", "State", false),
    CITY("city", "City", false),
    POSTAL_CODE("postalCode", "Postal Code", false),
    ORGANIZATION_ADMIN("adminName", "Organization Admin", true),
    ADMIN_EMAIL("adminEmail", "Email Address", false),
    ADMIN_PHONE_NUMBER("adminPhoneNumber", "Phone Number", true),
    APPROVAL_STATUS("approvalStatus", "Approval Status", true),
    ORGANIZATION_SUPER_ADMIN("organizationSuperAdmin", "Organization Super Admin", false),
    ORGANIZATION_DATE_CREATED("dateCreated", "Date Created", true),
    ;

    private String key;
    private String label;
    private boolean display;

    private static Map<String, OrganizationHeaderEnum> keyMap = new ConcurrentHashMap<>();

    OrganizationHeaderEnum(String key, String label, boolean display) {

        this.key = key;
        this.label = label;
        this.display = display;
    }

    static {
        for (OrganizationHeaderEnum headerEnum: values()) {
            keyMap.put(headerEnum.getKey(), headerEnum);
        }
    }

    public static OrganizationHeaderEnum fromKey(String key) {
        return key != null ? keyMap.get(key) : null;
    }

    public static List<OrganizationHeaderEnum> getOrganizationHeaderEnums(boolean defaultAllView) {
        List<OrganizationHeaderEnum> organizationHeaderEnums = new ArrayList<>();

        for (OrganizationHeaderEnum headerEnum : values()) {
            if (defaultAllView || headerEnum.display) {
                organizationHeaderEnums.add(headerEnum);
            }
        }

        return organizationHeaderEnums;

    }
}
