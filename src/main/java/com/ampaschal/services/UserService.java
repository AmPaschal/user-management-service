package com.ampaschal.services;

import com.ampaschal.entities.EmailParam;
import com.ampaschal.entities.GetUserListParam;
import com.ampaschal.entities.PasswordPolicyResult;
import com.ampaschal.entities.RequestHeaderContext;
import com.ampaschal.entities.SmsParam;
import com.ampaschal.entities.UserIdentifierParam;
import com.ampaschal.enums.Actions;
import com.ampaschal.enums.DatabaseCollections;
import com.ampaschal.enums.MessageMedium;
import com.ampaschal.enums.SettingEnum;
import com.ampaschal.mongo.EmailVerificationToken;
import com.ampaschal.mongo.UserCredential;
import com.ampaschal.mongo.Users;
import com.ampaschal.repositories.DataRepository;
import com.ampaschal.repositories.SettingsRepository;
import com.ampaschal.restartifacts.BaseUserRequest;
import com.ampaschal.restartifacts.CreateUserRequest;
import com.ampaschal.restartifacts.CreateUserResponse;
import com.ampaschal.restartifacts.GetUserListRequest;
import com.ampaschal.restartifacts.GetUserListResponse;
import com.ampaschal.restartifacts.LoginRequest;
import com.ampaschal.restartifacts.LoginResponse;
import com.ampaschal.restartifacts.ResponseBundleCode;
import com.ampaschal.restartifacts.ResponseCode;
import com.ampaschal.restartifacts.UpdateUserRequest;
import com.ampaschal.restartifacts.UserData;
import com.ampaschal.utils.Bcrypt;
import com.ampaschal.utils.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequestScoped
public class UserService {

    @Inject
    DataRepository dataRepository;

    @Inject
    SettingsRepository settingsRepository;

    @Inject
    Event<EmailParam> emailServiceEvent;

    @Inject
    Event<SmsParam> smsServiceEvent;

    @Inject
    Utility utility;

    @Inject
    RequestHeaderContext context;

    /*
     * The injected fields above were declared with package private as recommended by quarkus
     * */


    public CreateUserResponse createUser(CreateUserRequest request) {
        CreateUserResponse response = new CreateUserResponse();

        if (!utility.validateEmail(request.getEmail(), false)) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.INVALID_EMAIL, context.getLocale());
        }

        if (!utility.validatePhoneNumber(request.getPhoneNumber(), false)) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.INVALID_PHONE_NUMBER, context.getLocale());
        }

        if (!validateNameFields(request.getFirstName(), request.getLastName(), request.getMiddleName())) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.INVALID_INPUT_NAME, context.getLocale());
        }

        request.setPhoneNumber(utility.getInternationalNumberFormat(request.getPhoneNumber()));

        UserIdentifierParam param = checkForUniqueIdentifiersInRequest(request);

        if (param.getUser() != null) {
            String responseDescription = utility.getFormattedResponseDescription(ResponseBundleCode.USER_ALREADY_EXISTS, param.getIdentifier(), context.getLocale());
            return utility.updateResponseData(response, ResponseCode.ERROR, responseDescription);
        }

        boolean emailVerification = settingsRepository.getSettingValueBooleanPrimitive(SettingEnum.PERFORM_EMAIL_VERIFICATION, request.getProductId());

        Users user = createUserPojo(request);
        InsertOneResult saveResult = dataRepository.save(user, DatabaseCollections.USERS.getName(), Users.class);

        if (!saveResult.wasAcknowledged()) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.ERROR_SAVING_USER, context.getLocale());
        }

        sendSuccessfulNotification(Actions.CREATE_USER, user, request.getPassword(), emailVerification);

        response.setUser(getUserData(user));
        return utility.updateResponseData(response, ResponseCode.SUCCESS, ResponseBundleCode.SUCCESS_CREATING_USER, context.getLocale());

    }

    private UserData getUserData(Users user) {
        UserData userData = new UserData();
        userData.setUserId(user.getUserId());
        userData.setEmail(user.getEmail());
        userData.setPhoneNumber(user.getPhoneNumber());
        userData.setFirstName(user.getFirstName());
        userData.setLastName(user.getLastName());
        userData.setMiddleName(user.getMiddleName());
        userData.setBvn(user.getBvn());
        userData.setNin(user.getNin());
        userData.setGender(user.getGender());
        userData.setTitle(user.getTitle());
        userData.setUsername(user.getUsername());
        userData.setOrgId(user.getOrgId());
        if (user.getDateOfBirth() != null) {
            userData.setDateOfBirth(utility.getFormattedStringFromDate(user.getDateOfBirth()));
        }
        return userData;
    }

    private boolean validateNameFields(String firstName, String lastName, String middleName) {
        return utility.validateName(firstName, false) && utility.validateName(lastName, false)
                && utility.validateName(middleName, false);
    }


    private void sendSuccessfulNotification(Actions actionType, Users user, String password, boolean emailVerification) {
        String loginLink = "";
        String productSenderEmail = "";

        String[] mediumList;

        if (emailVerification) {
            mediumList = new String[]{MessageMedium.EMAIL.name()};
        } else {
            String mediums = settingsRepository.getSettingValue(SettingEnum.MESSAGE_SENDING_MEDIUM);
            mediumList = mediums.split(";");
        }

        for (String medium : mediumList) {

            MessageMedium messageMedium = MessageMedium.valueOf(medium);

            if (messageMedium == MessageMedium.EMAIL && user.getEmail() != null) {

                EmailParam emailParam = getEmailParam(user, password, emailVerification, loginLink, productSenderEmail, actionType);
                emailServiceEvent.fireAsync(emailParam);

            } else if (messageMedium == MessageMedium.SMS && user.getPhoneNumber() != null) {
                SmsParam smsParam = getSmsParam(user.getPhoneNumber(),
                        user.getFirstName(), user.getEmail(), password, loginLink, actionType);
                smsServiceEvent.fireAsync(smsParam);
            }
        }
    }

    private String getLoginLink() {
        return settingsRepository.getSettingValue(SettingEnum.BASE_PRODUCT_LOGIN_LINK);
    }

    private String getFailedRedirectLink() {
        return settingsRepository.getSettingValue(SettingEnum.FAILED_EMAIL_VERIFICATION_URL);
    }

    private EmailParam getEmailParam(Users user
            , String password, boolean emailVerification, String loginLink, String senderAddress, Actions actionType) {

        if (emailVerification) {
            return getEmailParamForVerification(user, senderAddress);
        }
        EmailParam emailParam = new EmailParam();
        emailParam.setSenderAddress(senderAddress);
        emailParam.setSenderName("Our Product");
        emailParam.setToAddress(user.getEmail());
        emailParam.setSubject("Get started with Us");

        emailParam.setMessage(utility.getEmailMessageForSuccessfulAccountCreation
                (user.getFirstName(), user.getEmail(), password, loginLink));

        return emailParam;
    }

    private String generateUserPassword() {
        PasswordPolicyResult passwordPolicy = getPasswordPolicy();
        return utility.generatePassword(passwordPolicy);
    }

    private EmailParam getEmailParamForVerification(Users user, String senderAddress) {

        int expiryInHours = settingsRepository.getSettingValueIntPrimitive(SettingEnum.VERIFICATION_LINK_EXPIRY);
        String verificationLink = generateVerificationLink(user.getUserId(), expiryInHours);
        EmailParam emailParam = new EmailParam();
        emailParam.setSenderAddress(senderAddress);
        emailParam.setSenderName("");
        emailParam.setMessage(utility.getEmailMessageForEmailVerification(null, user.getFirstName(), verificationLink, expiryInHours));
        emailParam.setSubject("Get started with ");
        emailParam.setToAddress(user.getEmail());
        return emailParam;
    }

    private String generateVerificationLink(String userId, int expiryInHours) {

        String token = UUID.randomUUID().toString();
        String verificationLink = utility.getVerificationLink(token);

        saveEmailVerificationToken(token, userId, expiryInHours);

        return verificationLink;

    }

    private void saveEmailVerificationToken(String token, String userId, int expiryInHours) {
        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setUserId(userId);
        verificationToken.setToken(token);
        verificationToken.setDateCreated(new Date());
        Date currentDate = new Date();
        verificationToken.setExpiry(Date.from(currentDate.toInstant().plus(Duration.ofHours(expiryInHours))));

        dataRepository.save(verificationToken, DatabaseCollections.EMAIL_VERIFICATION_TOKEN.getName(), EmailVerificationToken.class);
    }

    private SmsParam getSmsParam(String recipient, String firstName, String email, String password, String loginLink, Actions actionType) {

        SmsParam smsParam = new SmsParam();
        smsParam.setRecipient(recipient);

        smsParam.setMessage(utility.getSmsMessageForSuccessfulAccountCreation(
                firstName, email, password, loginLink));

        return smsParam;
    }


    private Users createUserPojo(CreateUserRequest request) {
        Users user = new Users();
        user.setUserId(UUID.randomUUID().toString());
        user.setActive(true);
        user.setEmail(request.getEmail().toLowerCase());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(utility.getInternationalNumberFormat(request.getPhoneNumber()));
        user.setMiddleName(request.getMiddleName());
        user.setGender(request.getGender());
        user.setUsername(request.getUsername());
        user.setTitle(request.getTitle());
        user.setRoles(new ArrayList<>());
        user.setNin(request.getNin());
        user.setBvn(request.getBvn());
        user.setOrgId(request.getOrgId());
        if (StringUtils.isNotBlank(request.getDateOfBirth())) {
            user.setDateOfBirth(utility.getDateFromString(request.getDateOfBirth()));
        }


        user.setCreateDate(new Date());

        user.setCredential(createUserCredential(request.getPassword()));

        return user;
    }

    private Users updateUserPojo(Users user, BaseUserRequest request, Actions action) {

        user.setEmail(request.getEmail().toLowerCase());
        user.setPhoneNumber(utility.getInternationalNumberFormat(request.getPhoneNumber()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setMiddleName(request.getMiddleName());
        user.setTitle(request.getTitle());
        user.setUsername(request.getUsername());
        user.setGender(request.getGender());
        user.setNin(request.getNin());
        user.setBvn(request.getBvn());

        String dateOfBirth = request.getDateOfBirth();
        if (StringUtils.isNotBlank(dateOfBirth)) {
            user.setDateOfBirth(utility.getDateFromString(dateOfBirth));
        }


        user.setLastModifiedDate(new Date());


        if (request.getExtraData() == null) {
            return user;
        }

        if (user.getExtraData() == null) {
            user.setExtraData(new HashMap<>());
        }
        request.getExtraData().forEach((key, value) ->
                user.getExtraData().put(key, value)
        );

        return user;
    }

    private UserCredential createUserCredential(String password) {
        UserCredential credential = new UserCredential();
        String hashedPassword = Bcrypt.hashpw(password, Bcrypt.gensalt());
        credential.setPassword(hashedPassword);
        credential.setChangePasswordOnLogin(false);
        credential.setPasswordUpdateTime(new Date());
        return credential;
    }

    private UserIdentifierParam checkForUniqueIdentifiersInRequest(CreateUserRequest request) {

        Document userRequestDocument;
        try {
            userRequestDocument = Document.parse(new ObjectMapper().writeValueAsString(request));
        } catch (JsonProcessingException ex) {
            log.error("Error converting object to document: ", ex);
            return new UserIdentifierParam();
        }

        return checkForUniqueIdentifiers(userRequestDocument);
    }

    private UserIdentifierParam checkForUniqueIdentifiersInUserObject(Users user) {

        Document userDocument;
        try {
            userDocument = Document.parse(new ObjectMapper().writeValueAsString(user));
        } catch (JsonProcessingException ex) {
            log.error("Error converting object to document: ", ex);
            return new UserIdentifierParam();
        }

        return checkForUniqueIdentifiers(userDocument);
    }

    private UserIdentifierParam checkForUniqueIdentifiers(Document userRequestDocument) {
        String settingValue = settingsRepository.getSettingValue(SettingEnum.UNIQUE_IDENTIFIERS);

        String[] uniqueIdentifiers = settingValue.split(";");

        for (String identifier : uniqueIdentifiers) {
            String idValue = userRequestDocument.getString(identifier);

            if (StringUtils.isBlank(idValue)) {
                continue;
            }

            if (identifier.equalsIgnoreCase("email")) {
                idValue = idValue.toLowerCase();
            }

            Users user = dataRepository.getUserByIdentifier(identifier, idValue);

            if (user != null) {
                return new UserIdentifierParam(user, identifier);
            }
        }

        return new UserIdentifierParam();
    }

    private Users getUserByUniqueIdentifiers(String userId) {

        String settingValue = settingsRepository.getSettingValue(SettingEnum.UNIQUE_IDENTIFIERS);

        String[] uniqueIdentifiers = settingValue.split(";");

        for (String identifier : uniqueIdentifiers) {

            if (identifier.equalsIgnoreCase("email")) {
                userId = userId.toLowerCase();
            }

            Users user = dataRepository.getUserByIdentifier(identifier, userId);

            if (user != null) {
                return user;
            }
        }

        return null;
    }

    public CreateUserResponse updateUser(UpdateUserRequest request) {
        CreateUserResponse response = new CreateUserResponse();

        if (!utility.validateEmail(request.getEmail(), false)) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.INVALID_EMAIL, context.getLocale());
        }

        if (!utility.validatePhoneNumber(request.getPhoneNumber(), false)) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.INVALID_PHONE_NUMBER, context.getLocale());
        }


        if (!validateNameFields(request.getFirstName(), request.getLastName(), request.getMiddleName())) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.INVALID_INPUT_NAME, context.getLocale());
        }


        Users user = dataRepository.getUserByUserId(request.getUserId());

        if (user == null) {
            user = getUserByUniqueIdentifiers(request.getUserId());
        }

        if (user == null) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.USER_NOT_FOUND, context.getLocale());
        }

        Users updatedUser = updateUserPojo(user, request, Actions.UPDATE_USER);

        UserIdentifierParam userParam = checkForUniqueIdentifiersInUserObject(updatedUser);

        if (userParam.getUser() != null && !userParam.getUser().getUserId().equals(updatedUser.getUserId())) {
            String responseDescription = utility.getFormattedResponseDescription(ResponseBundleCode.USER_ALREADY_EXISTS, userParam.getIdentifier(), context.getLocale());
            return utility.updateResponseData(response, ResponseCode.ERROR, responseDescription);
        }

        UpdateResult updateResult = dataRepository.replaceDocumentById(
                DatabaseCollections.USERS.getName(), Users.class, updatedUser, user.getId()
        );

        if (updateResult.wasAcknowledged() && updateResult.getModifiedCount() > 0) {
            response.setUser(getUserData(user));
            return utility.updateResponseData(response, ResponseCode.SUCCESS, ResponseBundleCode.SUCCESS_UPDATING_USER, context.getLocale());
        }

        return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.ERROR_UPDATING_USER, context.getLocale());
    }

    public PasswordPolicyResult getPasswordPolicy() {

        PasswordPolicyResult result = new PasswordPolicyResult();

        result.setExpiryDuration(settingsRepository.getSettingValueInt(SettingEnum.PASSWORD_POLICY_EXPIRY_DURATION));
        result.setExpiryEnabled(settingsRepository.getSettingValueBooleanPrimitive(SettingEnum.PASSWORD_POLICY_EXPIRY_ENABLED));
        result.setHistoryLimit(settingsRepository.getSettingValueInt(SettingEnum.PASSWORD_POLICY_HISTORY_LIMIT));
        result.setMinAllowedAlphabetCount(settingsRepository.getSettingValueInt(SettingEnum.PASSWORD_POLICY_MIN_ALLOWED_ALPHABET_COUNT));
        result.setMinAllowedDigitCount(settingsRepository.getSettingValueInt(SettingEnum.PASSWORD_POLICY_MIN_ALLOWED_DIGIT_COUNT));
        result.setMinAllowedLowerCaseCharacterCount(settingsRepository.getSettingValueInt(SettingEnum.PASSWORD_POLICY_MIN_ALLOWED_LOWER_CASE_CHARACTER_COUNT));
        result.setMinAllowedSpecialCharacterCount(settingsRepository.getSettingValueInt(SettingEnum.PASSWORD_POLICY_MIN_ALLOWED_SPECIAL_CHARACTER_COUNT));
        result.setMinAllowedUpperCaseCharacterCount(settingsRepository.getSettingValueInt(SettingEnum.PASSWORD_POLICY_MIN_ALLOWED_UPPER_CASE_CHARACTER_COUNT));
        result.setMinLength(settingsRepository.getSettingValueInt(SettingEnum.PASSWORD_POLICY_MIN_LENGTH));
        result.setReuseEnabled(settingsRepository.getSettingValueBooleanPrimitive(SettingEnum.PASSWORD_POLICY_REUSE_ENABLED));

        return result;
    }

    public Response verifyEmail(String token) {

        EmailVerificationToken emailVerificationToken = dataRepository.getEmailVerificationToken(token);

        if (emailVerificationToken == null) {
            return buildRedirectResponse(getFailedRedirectLink());
        }

        if (emailVerificationToken.getExpiry() != null && emailVerificationToken.getExpiry().before(new Date())) {
            return buildRedirectResponse(getFailedRedirectLink());
        }

        if (emailVerificationToken.isTokenUsed()) {
            return buildRedirectResponse(getFailedRedirectLink());
        }

        Users user = dataRepository.getUserByUserId(emailVerificationToken.getUserId());

        user.setEmailVerified(true);
        user.setEmailVerifiedDate(new Date());

        UpdateResult updateResult = dataRepository.replaceDocumentById(DatabaseCollections.USERS.getName(), Users.class, user, user.getId());

        if (!updateResult.wasAcknowledged() || updateResult.getModifiedCount() < 1) {
            return buildRedirectResponse(getFailedRedirectLink());
        }

        emailVerificationToken.setTokenUsed(true);
        emailVerificationToken.setDateVerified(new Date());

        dataRepository.replaceDocumentById(DatabaseCollections.EMAIL_VERIFICATION_TOKEN.getName(),
                EmailVerificationToken.class, emailVerificationToken, emailVerificationToken.getId());

        return buildRedirectResponse(getLoginLink());


    }

    private Response buildRedirectResponse(String redirect) {
        Response.ResponseBuilder builder = Response.seeOther(URI.create(redirect));
        return builder.build();
    }


    public LoginResponse loginUser(LoginRequest loginRequest) {
        LoginResponse loginResponse = new LoginResponse();

        Users user = dataRepository.getUserByEmail(loginRequest.getEmail());

        if (user == null) {
            return utility.updateResponseData(loginResponse, ResponseCode.ERROR, ResponseBundleCode.INVALID_USER, context.getLocale());
        } else if (!user.isActive()) {
            return utility.updateResponseData(loginResponse, ResponseCode.ERROR, ResponseBundleCode.INACTIVE_USER, context.getLocale());
        } else if (user.isLocked()) {
            return utility.updateResponseData(loginResponse, ResponseCode.ERROR, ResponseBundleCode.LOCKED_USER, context.getLocale());
        }

        boolean matched = Bcrypt.checkpw(loginRequest.getPassword(), user.getCredential().getPassword());

        if (!matched) {
            return handlePasswordMismatch(user, loginResponse);
        }

        UserData userData = getUserData(user);
        loginResponse.setUser(userData);

        return handlePasswordMatch(user, loginResponse);
    }

    private LoginResponse handlePasswordMatch(Users user, LoginResponse loginResponse) {

        UserCredential credential = user.getCredential();
        credential.setLastSuccessfulLogin(new Date());
        credential.setFailedLoginCount(0L);

        user.setCredential(credential);

        dataRepository.replaceDocumentById(DatabaseCollections.USERS.getName(), Users.class, user, user.getId());

        return utility.updateResponseData(loginResponse, ResponseCode.SUCCESS, ResponseBundleCode.SUCCESS_LOGIN, context.getLocale());
    }

    private LoginResponse handlePasswordMismatch(Users user, LoginResponse loginResponse) {
        ResponseBundleCode bundleCode;
        UserCredential credential = user.getCredential();
        credential.setFailedLoginCount(credential.getFailedLoginCount() + 1);
        if (credential.getFailedLoginCount() >= 3) {
            user.setLocked(true);
            bundleCode = ResponseBundleCode.LOCKED_USER;
        } else {
            bundleCode = ResponseBundleCode.INVALID_PASSWORD;
        }

        credential.setLastFailedLogin(new Date());
        user.setCredential(credential);

        dataRepository.replaceDocumentById(DatabaseCollections.USERS.getName(), Users.class, user, user.getId());

        return utility.updateResponseData(loginResponse, ResponseCode.ERROR, bundleCode, context.getLocale());
    }

    public GetUserListResponse getUserList(GetUserListRequest request) {
        GetUserListResponse response = new GetUserListResponse();

        int size = request.getPageSize() != null ? request.getPageSize() : 15;
        int pageNo = request.getPageNo() != null ? request.getPageNo() : 0;
        int start = size * pageNo;

        GetUserListParam param = new GetUserListParam();
        param.setPageSize(size);
        param.setPageStart(start);
        param.setActive(request.getActive());
        param.setSearch(request.getSearch());

        List<Users> userList = dataRepository.getUsers(param);

        List<UserData> users = userList.stream().map(this::getUserData).collect(Collectors.toList());

        response.setUsers(users);


        return utility.updateResponseData(response, ResponseCode.SUCCESS, ResponseBundleCode.SUCCESS, context.getLocale());
    }


    public LoginResponse unlockUser(String userId) {
        LoginResponse loginResponse = new LoginResponse();

        Users user = dataRepository.getUserByUserId(userId);

        if (user == null) {
            return utility.updateResponseData(loginResponse, ResponseCode.ERROR, ResponseBundleCode.USER_NOT_FOUND, context.getLocale());
        } else if (!user.isActive()) {
            return utility.updateResponseData(loginResponse, ResponseCode.ERROR, ResponseBundleCode.INACTIVE_USER, context.getLocale());
        } else if (!user.isLocked()) {
            return utility.updateResponseData(loginResponse, ResponseCode.ERROR, ResponseBundleCode.USER_NOT_LOCKED, context.getLocale());
        }

        user.setLocked(false);
        user.getCredential().setFailedLoginCount(0L);

        dataRepository.replaceDocumentById(DatabaseCollections.USERS.getName(), Users.class, user, user.getId());

        UserData userData = getUserData(user);
        loginResponse.setUser(userData);

        return utility.updateResponseData(loginResponse, ResponseCode.SUCCESS, ResponseBundleCode.SUCCESS, context.getLocale());
    }
}