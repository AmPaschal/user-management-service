package com.ampaschal.enums;

public enum SettingEnum implements ISettingEnum {

    UNIQUE_IDENTIFIERS("UNIQUE-IDENTIFIERS", "email;phoneNumber", "application name"),
    MESSAGE_SENDING_MEDIUM("MESSAGE-SENDING-MEDIUM", "EMAIL", "medium for sending messages on successful account creation"),
    BASE_PRODUCT_LOGIN_LINK("BASE-PRODUCT-LOGIN-LINK", "", "Default login link to be used if there's no product login link"),
    FAILED_EMAIL_VERIFICATION_URL("FAILED-EMAIL-VERIFICATION-URL", "", "Default link on failed email verification"),
    SEND_EMAIL_URL("SEND-EMAIL-URL", "", "Url to send email"),
    SEND_SMS_URL("SEND-SMS-URL", "", "Url to send sms"),
    VERIFICATION_LINK_EXPIRY("VERIFICATION-LINK-EXPIRY", "24", "Time in hours to expiration of email verification link"),
    VERIFICATION_LINK_TEMPLATE("VERIFICATION-LINK-TEMPLATE", "", "Template for the verification link"),
    SMS_USER_ACCOUNT_CREATION("SMS-USER-ACCOUNT-CREATION", "Dear %s, Your account has been created on %s. \n Your Email is %s and your password is %s. \n Click %s to login to your account.", "SMS template for successful sign up"),
    PASSWORD_POLICY_MIN_LENGTH("PASSWORD-POLICY-MIN-LENGTH", "8", "Password policy min length"),
    PASSWORD_POLICY_MIN_ALLOWED_ALPHABET_COUNT("PASSWORD-POLICY-MIN-ALLOWED-ALPHABET-COUNT", "1", "Password policy min allowed alphabet count"),
    PASSWORD_POLICY_MIN_ALLOWED_DIGIT_COUNT("PASSWORD-POLICY-MIN-ALLOWED-DIGIT-COUNT", "1", "Password policy min allowed digit count"),
    PASSWORD_POLICY_MIN_ALLOWED_SPECIAL_CHARACTER_COUNT("PASSWORD-POLICY-MIN-ALLOWED-SPECIAL-CHARACTER-COUNT", "1", "Password policy min allowed special case character count"),
    PASSWORD_POLICY_MIN_ALLOWED_UPPER_CASE_CHARACTER_COUNT("PASSWORD-POLICY-MIN-ALLOWED-UPPER-CASE-CHARACTER-COUNT", "1", "Password policy min allowed upper case character count"),
    PASSWORD_POLICY_MIN_ALLOWED_LOWER_CASE_CHARACTER_COUNT("PASSWORD-POLICY-MIN-ALLOWED-LOWER-CASE-CHARACTER-COUNT", "1", "Password policy min allowed lower case character count"),
    PASSWORD_POLICY_EXPIRY_ENABLED("PASSWORD-POLICY-EXPIRY-ENABLED", "true", "Password policy expiry enabled"),
    PASSWORD_POLICY_EXPIRY_DURATION("PASSWORD-POLICY-EXPIRY-DURATION", "90", "Password policy expiry duration"),
    PASSWORD_POLICY_REUSE_ENABLED("PASSWORD-POLICY-REUSE-ENABLED", "false", "Password policy reuse enabled"),
    PASSWORD_POLICY_HISTORY_LIMIT("PASSWORD-POLICY-HISTORY-LIMIT", "3", "Password policy history limit"),
    PERFORM_EMAIL_VERIFICATION("PERFORM-EMAIL-VERIFICATION", "true", "Perform email verification on successful account creation"),
    ORGANIZATION_HEADER_KEYS_ALL("ORGANIZATION-HEADER-KEYS-ALL", "", "Organization Header keys for viewing organization details"),
    ORGANIZATION_HEADER_KEYS_SNAPSHOT("ORGANIZATION-HEADER-KEYS-SNAPSHOT", "", "Organization Header keys for viewing organization summary in table"),
    ENABLE_RC_NUMBER_UPDATE("ENABLE-RC-NUMBER-UPDATE", "true", "A setting that allows organizations change their RC Number during update")
    ;

    private final String name;
    private final String value;
    private final String description;

    SettingEnum(String name, String value, String description) {
        this.name = name;
        this.value = value;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

}