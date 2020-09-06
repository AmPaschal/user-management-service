package com.ampaschal.utils;

import com.ampaschal.entities.PasswordPolicyResult;
import com.ampaschal.entities.RequestHeaderContext;
import com.ampaschal.enums.SettingEnum;
import com.ampaschal.repositories.SettingsRepository;
import com.ampaschal.restartifacts.BaseResponse;
import com.ampaschal.restartifacts.ResponseBundleCode;
import com.ampaschal.restartifacts.ResponseCode;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Amusuo Paschal C.
 * @since 7/21/2020 10:58 PM
 */

@Slf4j
@RequestScoped
public class Utility {

    public static final String UNKNOWN = "unknown";
    @Inject
    Template emailVerificationTemplate;

    @Inject
    Template successfulAccountCreationTemplate;

    @Inject
    Template successfulProductSignUpTemplate;

    @Inject
    SettingsRepository settingsRepository;

    @Inject
    RequestHeaderContext context;

    @Inject
    ResourceBundleUtil resourceBundleUtil;

    private String emailRegex = "^([\\w-]+(?:\\.[\\w-]+)*)@((?:[\\w-]+\\.)*\\w[\\w-]{0,66})\\.([a-z]{2,6}(?:\\.[a-z]{2})?)$";
    private String phoneNumberRegex = "[+][0-9]{2}[1-9]{1}[0-9]{8,14}";
    private String nameRegex = "(?i)(^[a-z])((?![ .,'-]$)[a-z .,'-]){0,24}$";


    public String getClientIpAddr() {
        MultivaluedMap<String, String> httpHeaders = context.getHttpHeaders();
        String ip = httpHeaders.getFirst("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = httpHeaders.getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = httpHeaders.getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = httpHeaders.getFirst("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = httpHeaders.getFirst("HTTP_X_FORWARDED_FOR");
        }
        return ip;
    }

    public String getDeviceType() {
        return context.getHttpHeaders().getFirst("User-Agent");
    }

    public String getEmailMessageForSuccessfulAccountCreation(String firstName, String email, String password, String loginLink) {

        TemplateInstance template = successfulAccountCreationTemplate.data("firstName", firstName)
                .data("email", email)
                .data("password", password)
                .data("loginLink", loginLink)
                .data("userCreated", true);
        return template.render();
    }

    public String getEmailMessageForEmailVerification(String productName, String firstName, String verificationLink, int expiryInHours) {

        return emailVerificationTemplate.data("firstName", firstName)
                .data("productName", productName)
                .data("verificationLink", verificationLink)
                .data("expiryHours", expiryInHours)
                .render();

    }

    public String generatePassword(PasswordPolicyResult result) {
        PasswordGenerator generator = new PasswordGenerator();
        List<CharacterRule> characterRules = new ArrayList<>();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(result.getMinAllowedLowerCaseCharacterCount());
        characterRules.add(lowerCaseRule);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(result.getMinAllowedUpperCaseCharacterCount());
        characterRules.add(upperCaseRule);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(result.getMinAllowedDigitCount());
        characterRules.add(digitRule);

        CharacterData alphabetChars = EnglishCharacterData.Alphabetical;
        CharacterRule alphabetRule = new CharacterRule(alphabetChars);
        alphabetRule.setNumberOfCharacters(result.getMinAllowedAlphabetCount());
        characterRules.add(alphabetRule);

        CharacterData specialChars = new CharacterData() {
            @Override
            public String getErrorCode() {
                return EnglishCharacterData.Special.getErrorCode();
            }

            @Override
            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        };
        CharacterRule specialCharacterRule = new CharacterRule(specialChars);
        specialCharacterRule.setNumberOfCharacters(result.getMinAllowedSpecialCharacterCount());
        characterRules.add(specialCharacterRule);

        int passwordLength = result.getMinLength() > 10 ? result.getMinLength() : 10;
        return generator.generatePassword(passwordLength, characterRules);
    }

    public String getSmsMessageForSuccessfulAccountCreation(String firstName, String email, String password, String loginLink) {

        String smsMessage;

        String messageUserTemplate = settingsRepository.getSettingValue(SettingEnum.SMS_USER_ACCOUNT_CREATION);
        smsMessage = String.format(messageUserTemplate, firstName, "", email, password, loginLink);
        return smsMessage;
    }

    public boolean validateEmail(String email, boolean compulsory) {
        if (StringUtils.isBlank(email)) {
            return !compulsory;
        }
        return validateWithRegex(email, emailRegex);
    }

    public boolean validatePhoneNumber(String phoneNumber, boolean compulsory) {
        if (StringUtils.isBlank(phoneNumber)) {
            return !compulsory;
        }

        String standardNumber = getInternationalNumberFormat(phoneNumber);

        return standardNumber != null && validateWithRegex(standardNumber, phoneNumberRegex);
    }

    public String getInternationalNumberFormat(String phoneNumberStr) {

        if (StringUtils.isBlank(phoneNumberStr)) {
            return null;
        }

        if (phoneNumberStr.startsWith("+") && validateWithRegex(phoneNumberStr, phoneNumberRegex)) {
            return phoneNumberStr.replace(" ", "");
        }

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber;
        try {
            phoneNumber = phoneUtil.parse(phoneNumberStr, "NG");
        } catch (NumberParseException e) {
            log.error("NumberParseException was thrown: " + e.toString());
            return null;
        }

        return phoneUtil.isValidNumber(phoneNumber) ?
                phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164) : null;
    }

    private boolean validateWithRegex(String phoneNumber, String regex) {
        Pattern matchPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher match = matchPattern.matcher(phoneNumber);
        return match.matches();
    }

    public boolean validateName(String name, boolean compulsory) {
        if (StringUtils.isBlank(name)) {
            return !compulsory;
        }

        return validateWithRegex(name, nameRegex);
    }

    public String getVerificationLink(String token) {
        String verificationLinkTemplate = settingsRepository.getSettingValue(SettingEnum.VERIFICATION_LINK_TEMPLATE);

        return String.format(verificationLinkTemplate, token);
    }

    public  <T extends BaseResponse> T updateResponseData(T baseResponse, ResponseCode responseCode, ResponseBundleCode responseBundleCode, Locale locale) {
        String resourceMessage = resourceBundleUtil.getMessage(responseBundleCode, locale);
        return updateResponseData(baseResponse, responseCode, resourceMessage);
    }

    public  <T extends BaseResponse> T updateResponseData(T baseResponse, ResponseCode responseCode, String description) {

        baseResponse.assignResponseCode(responseCode.getCode(), description);

        return baseResponse;
    }

    public Date getDateFromString(String dateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date;
        try {
            date = inputFormat.parse(dateString);
        } catch (ParseException ex) {
            log.error("Error thrown while parsing date, {}", ex);
            date = null;
        }
        return date;
    }

    public String getFormattedStringFromDate(Date date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        return date != null ? inputFormat.format(date) : null;
    }

    public String getFormattedResponseDescription(ResponseBundleCode responseBundleCode, String identifier, Locale locale) {
        String translatedIdentifier = resourceBundleUtil.getMessage(identifier, identifier,  locale);
        String resourceMessage = resourceBundleUtil.getMessage(responseBundleCode, locale);

        return String.format(resourceMessage, translatedIdentifier);


    }
}
