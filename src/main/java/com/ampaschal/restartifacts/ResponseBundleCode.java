/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ampaschal.restartifacts;

import com.ampaschal.enums.IResponseBundleCode;
import lombok.Getter;

/**
 *
 * @author Amusuo Paschal
 *
 */
@Getter
public enum ResponseBundleCode implements IResponseBundleCode {

	SUCCESS("success", "Success"),
    ERROR("error", "Error"),
    SUCCESS_CREATING_USER("success-creating-user", "User Created Successfully"),
    SUCCESS_UPDATING_USER("success-updating-user", "User Updated Successfully"),
    SUCCESS_SIGNUP_PRODUCT("success-signup-product", "User successfully signed up to product"),
    SUCCESS_CREATING_ORGANIZATION("success-creating-organization", "Organization created successfully"),
    SUCCESS_UPDATING_ORGANIZATION("success-updating-organization", "Organization updated successfully"),
    SUCCESS_ORGANIZATION_PRODUCT_SIGNUP("success-organization-product-signup", "Organization successfully signed up to product"),
    SUCCESS_ACTIVATING_ORGANIZATION("success-activating-organization", "Organization activated successfully"),
    SUCCESS_DEACTIVATING_ORGANIZATION("success-deactivating-organization", "Organization deactivated successfully"),
    SUCCESS_LOGIN("success-login", "User login successful"),
    INVALID_INPUT("invalid-input", "Request contains missing or invalid fields"),
    INVALID_EMAIL("invalid-email", "Request contains an invalid email"),
    INVALID_PHONE_NUMBER("invalid-phone-number", "Request contains an invalid phone number"),
    INVALID_INPUT_NAME("invalid-input-name", "Request contains an invalid name"),
    NO_RECORDS_FOUND("no-records-found", "No record was found in the requested document"),
    PRODUCT_NOT_FOUND("product-not-found", "No product was found with inputted product id"),
    FORM_CONFIG_NOT_FOUND("form-config-not-found", "No form configuration was found with inputted product id and form type"),
    USER_ALREADY_EXISTS("user-already-exists", "A user with same %s already exists"),
    USER_NOT_FOUND("user-not-found", "No user found with inputted userId"),
    INVALID_ACCOUNT_CREATOR("invalid-account-creator", "The creator entered is invalid"),
    ERROR_SAVING_USER("error-saving-user", "An error occurred while saving user"),
    ERROR_UPDATING_USER("error-updating-user", "An error occurred while updating user info"),
    ERROR_ADDING_PRODUCT("error-adding-product", "An error occurred while signing up for product"),
    ERROR_VERIFYING_EMAIL("error-verifying-email", "An error occurred while verifying email"),
    PRODUCT_EXISTS_FOR_USER("product-exists-for-user", "The user has already signed up for the specified product"),
    PRODUCT_NOT_EXISTS_FOR_USER("product-not-exists-for-user", "The user is not signed up for the specified product"),
    INVALID_VERIFICATION_TOKEN("invalid-verification-token", "The token entered is either invalid or expired"),
    ERROR_CREATING_RESOURCE("error-creating-file", "There was an error creating the bulk upload template"),
    RESOURCE_NOT_FOUND("resource-not-found", "The requested resource could not be found"),
    INVALID_EXCEL_TEMPLATE("invalid-excel-template", "Please, use the same excel template downloaded to upload the user data without modifying it's name or structure"),
    RECORDS_NOT_WITHIN_LIMITS("records-not-within-limits", "The number of records uplaoded is not within the specified minimum and maximum limits"),
    ERROR_CREATING_UPDATED_WORKBOOK("error-creating-updated-workbook","There was an error creating updated workbook with report"),
    FORM_VERSION_MISMATCH("form-version-mismatch", "The form config has been updated. Kindly re-download the template and try again"),
    DUPLICATE_ORGANIZATION_FOUND("duplicate-organization-found", "An organisation with this Name and/or RC Number already exists."),
    DUPLICATE_ORGANIZATION_NAME_FOUND("duplicate-organization-name-found", "An organisation with this Name already exists."),
    ORGANIZATION_NOT_FOUND("organization-not-found", "No organization found with specified organization id"),
    INVALID_SUPER_ADMIN("super-admin-not-found", "You specified an invalid or no super admin user."),
    INVALID_ORGANIZATION_ADMIN("invalid-organization-admin", "The admin specified with user id was not found"),
    ORGANIZATION_ADMIN_NOT_SPECIFIED("organization-admin-not-specified", "No admin was specified for this organization"),
    ERROR_SAVING_ORGANIZATION("error-saving-organization", "There was an error saving this organization"),
    ERROR_UPDATING_ORGANIZATION("error-updating-organization", "There was an error updating this organization"),
    ORGANIZATION_PRODUCT_SIGNUP("organization-product-signup", "This organization has already signed up to the specified product"),
    ADMIN_ORGANIZATION_MATCH("admin-organization-match", "The specified admin already belongs to another organization"),
    ORGANIZATION_ALREADY_ACTIVE("organization-already-active", "This organization is already active."),
    ORGANIZATION_ALREADY_DEACTIVATED("organization-already-deactivated", "This organization has already been deactivated."),
    INVALID_FORM_CONFIGURATION("invalid-form-configuration", "An error occurred while parsing the form configuration JSON string"),
    INACTIVE_SUPER_ADMIN("inactive-super-admin", "The Super admin specified is inactive or has been deactivated"),
    INACTIVE_ADMIN("inactive-admin", "Your user account has been deactivated and you can not proceed with organization sign-up"),
    INACTIVE_ORGANIZATION("inactive-organization", "The organization is inactive"),
    INACTIVE_USER("inactive-user", "The user account specified is inactive"),
    INVALID_USER("invalid-user", "The username or password is incorrect"),
    INVALID_PASSWORD("invalid-password", "The username or password is incorrect"),
    LOCKED_USER("locked-user", "The user account is locked"),
    USER_NOT_LOCKED("user-not-locked", "The user account is not locked"),
    ;

    private final String key; // resource bundle key
    private final String defaultTemplate; // default bundle message if bundle file or key is not found

    ResponseBundleCode(String key, String defaultTemplate) {
        this.key = key;
        this.defaultTemplate = defaultTemplate;
    }

}