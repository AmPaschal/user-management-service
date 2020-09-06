package com.ampaschal.enums;

public enum DatabaseCollections {

    SETTINGS("settings"),
    PRODUCT_SETTINGS("product_settings"),
    USERS("users"),
    EMAIL_VERIFICATION_TOKEN("email_verification_tokens"),
    ORGANIZATIONS("organizations"),
    ;

    private String name;

    DatabaseCollections(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
