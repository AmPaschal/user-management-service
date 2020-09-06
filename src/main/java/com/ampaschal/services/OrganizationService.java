package com.ampaschal.services;

import com.ampaschal.entities.GetOrganizationListParam;
import com.ampaschal.entities.OrganizationActivationData;
import com.ampaschal.entities.OrganizationCreationActivityData;
import com.ampaschal.entities.OrganizationHeader;
import com.ampaschal.entities.RequestHeaderContext;
import com.ampaschal.enums.Actions;
import com.ampaschal.enums.DatabaseCollections;
import com.ampaschal.enums.OrganizationHeaderEnum;
import com.ampaschal.enums.SettingEnum;
import com.ampaschal.mongo.Organization;
import com.ampaschal.mongo.Users;
import com.ampaschal.repositories.DataRepository;
import com.ampaschal.repositories.SettingsRepository;
import com.ampaschal.restartifacts.ActivateOrganizationRequest;
import com.ampaschal.restartifacts.BaseOrganizationRequest;
import com.ampaschal.restartifacts.CreateOrganizationRequest;
import com.ampaschal.restartifacts.CreateOrganizationResponse;
import com.ampaschal.restartifacts.CreateUserRequest;
import com.ampaschal.restartifacts.GetOrganizationListRequest;
import com.ampaschal.restartifacts.GetOrganizationListResponse;
import com.ampaschal.restartifacts.GetOrganizationRequest;
import com.ampaschal.restartifacts.GetOrganizationResponse;
import com.ampaschal.restartifacts.OrganizationData;
import com.ampaschal.restartifacts.OrganizationMetaData;
import com.ampaschal.restartifacts.OrganizationProductSignUpRequest;
import com.ampaschal.restartifacts.ResponseBundleCode;
import com.ampaschal.restartifacts.ResponseCode;
import com.ampaschal.restartifacts.SignUpOrganizationRequest;
import com.ampaschal.restartifacts.UpdateOrganizationRequest;
import com.ampaschal.utils.Utility;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Amusuo Paschal C.
 * @since 8/17/2020 2:58 PM
 */

@Slf4j
@RequestScoped
public class OrganizationService {

    @Inject
    DataRepository dataRepository;

    @Inject
    SettingsRepository settingsRepository;

    @Inject
    UserService userService;

    @Inject
    Utility utility;

    @Inject
    RequestHeaderContext context;

    @Inject
    Event<OrganizationCreationActivityData> auditServiceEvent;

    @Inject
    Event<OrganizationActivationData> auditActivationEvent;

    private final Pattern commaPattern = Pattern.compile(",");

    public CreateOrganizationResponse createOrganization(BaseOrganizationRequest request) {

        CreateOrganizationResponse response = new CreateOrganizationResponse();



        Organization savedOrganization = StringUtils.isNotBlank(request.getRcNumber()) ?
                dataRepository.getOrganizationByNameOrRCNumber(request.getName(), request.getRcNumber()) :
                dataRepository.getOrganizationByName(request.getName());

        if (savedOrganization != null) {
            ResponseBundleCode bundleCode = StringUtils.isNotBlank(request.getRcNumber()) ? ResponseBundleCode.DUPLICATE_ORGANIZATION_FOUND : ResponseBundleCode.DUPLICATE_ORGANIZATION_NAME_FOUND;
            return utility.updateResponseData(response, ResponseCode.ERROR, bundleCode, context.getLocale());
        }

        String adminUserId;
        String superAdminUserId;

        SignUpOrganizationRequest organizationRequest = (SignUpOrganizationRequest) request;

        Users adminUser = dataRepository.getUserByUserId(organizationRequest.getAdminUserId());

        if (adminUser == null) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.INVALID_ORGANIZATION_ADMIN, context.getLocale());
        } else if (adminUser.getOrgId() != null) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.ADMIN_ORGANIZATION_MATCH, context.getLocale());
        } else if (!adminUser.isActive()) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.INACTIVE_ADMIN, context.getLocale());
        }
        adminUserId = adminUser.getUserId();
        superAdminUserId = adminUserId;

        Organization organization = createOrganizationEntity(request, adminUserId, superAdminUserId);

        if (StringUtils.isNotBlank(request.getProductId())) {
            updateOrganizationWithProduct(organization, request.getProductId());
        }

        InsertOneResult saveResult = dataRepository.save(organization, DatabaseCollections.ORGANIZATIONS.getName(), Organization.class);

        if (!saveResult.wasAcknowledged()) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.ERROR_SAVING_ORGANIZATION, context.getLocale());
        }

        updateAdminWithOrgId(adminUserId, organization.getOrgId());

        OrganizationCreationActivityData activityData = getOrganizationCreationActivityData(Actions.CREATE_ORGANIZATION, organization);
        auditServiceEvent.fireAsync(activityData);

        response.setOrganization(getOrganizationData(organization));
        return utility.updateResponseData(response, ResponseCode.SUCCESS, ResponseBundleCode.SUCCESS_CREATING_ORGANIZATION, context.getLocale());
    }

    private void updateAdminWithOrgId(String adminUserId, String orgId) {
        Users admin = dataRepository.getUserByUserId(adminUserId);
        admin.setOrgId(orgId);

        dataRepository.replaceDocumentById(DatabaseCollections.USERS.getName(), Users.class, admin, admin.getId());
    }

    private OrganizationCreationActivityData getOrganizationCreationActivityData(Actions action, Organization organization) {
        OrganizationCreationActivityData activityData = new OrganizationCreationActivityData();
        activityData.setName(organization.getName());
        activityData.setRcNumber(organization.getRcNumber());
        activityData.setActivityDate(new Date());
        activityData.setActivityPerformed(action.getActionName());
        activityData.setDeviceType(utility.getDeviceType());
        activityData.setIpAddress(utility.getClientIpAddr());

        return activityData;
    }

    private void updateOrganizationWithProduct(Organization organization, String productId) {
        List<String> products;
        if (organization.getProducts() == null) {
            products = new ArrayList<>();
        } else {
            products = organization.getProducts();
        }

        products.add(productId);
        organization.setProducts(products);
    }

    private Organization createOrganizationEntity(BaseOrganizationRequest request, String adminUserId, String superAdminUserId) {
        Organization organization = new Organization();

        organization.setName(request.getName().toUpperCase());
        organization.setAddress(request.getAddress());
        organization.setOrgId(UUID.randomUUID().toString());
        organization.setCity(request.getCity());
        organization.setIndustry(request.getIndustry());
        organization.setState(request.getState());
        organization.setCountry(request.getCountry());
        if (StringUtils.isNotBlank(request.getRcNumber())) {
            organization.setRcNumber(request.getRcNumber().toUpperCase());
            organization.setRcNumberVerificationStatus(request.isRcNumberVerified());
        }
        organization.setPostalCode(request.getPostalCode());
        organization.setWebsite(request.getWebsite());
        organization.setAdminUserId(adminUserId);
        organization.setExtraData(request.getExtraData());
        if (superAdminUserId != null) {
            organization.setSuperAdminUserId(superAdminUserId);
        } else {
            organization.setSuperAdminUserId(adminUserId);
        }
        organization.setPartner(request.isPartner());
        organization.setCreateDate(new Date());
        organization.setActive(true);

        return organization;

    }

    private CreateUserRequest getCreateUserRequest(CreateOrganizationRequest orgRequest) {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setEmail(orgRequest.getAdminEmail());
        userRequest.setPhoneNumber(orgRequest.getAdminPhoneNumber());
        userRequest.setFirstName(orgRequest.getAdminFirstName());
        userRequest.setLastName(orgRequest.getAdminLastName());

        return userRequest;
    }

    public CreateOrganizationResponse updateOrganization(UpdateOrganizationRequest request) {
        CreateOrganizationResponse response = new CreateOrganizationResponse();

        Organization savedOrganization = dataRepository.getOrganizationByOrgId(request.getOrgId());

        if (savedOrganization == null) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.ORGANIZATION_NOT_FOUND, context.getLocale());
        }

        if (!savedOrganization.isActive()) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.INACTIVE_ORGANIZATION, context.getLocale());
        }

        updateOrganizationDetails(savedOrganization, request);

        Organization organizationByName = StringUtils.isNotBlank(savedOrganization.getRcNumber()) ?
                dataRepository.getOrganizationByNameOrRCNumber(savedOrganization.getName(), savedOrganization.getRcNumber()) :
                dataRepository.getOrganizationByName(savedOrganization.getName());

        if (!organizationByName.getOrgId().equals(savedOrganization.getOrgId())) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.DUPLICATE_ORGANIZATION_FOUND, context.getLocale());
        }

        UpdateResult updateResult = dataRepository.replaceDocumentById(DatabaseCollections.ORGANIZATIONS.getName(), Organization.class, savedOrganization, savedOrganization.getId());

        if (!updateResult.wasAcknowledged()) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.ERROR_UPDATING_ORGANIZATION, context.getLocale());
        }

        OrganizationCreationActivityData activityData = getOrganizationCreationActivityData(Actions.UPDATE_ORGANIZATION, savedOrganization);
        auditServiceEvent.fireAsync(activityData);

        response.setOrganization(getOrganizationData(savedOrganization));
        return utility.updateResponseData(response, ResponseCode.SUCCESS, ResponseBundleCode.SUCCESS_UPDATING_ORGANIZATION, context.getLocale());
    }

    private OrganizationData getOrganizationData(Organization organization) {
        OrganizationData data = new OrganizationData();
        data.setName(organization.getName());
        data.setRcNumber(organization.getRcNumber());
        data.setIndustry(organization.getIndustry());
        data.setWebsite(organization.getWebsite());
        data.setAddress(organization.getAddress());
        data.setCity(organization.getCity());
        data.setState(organization.getState());
        data.setCountry(organization.getCountry());
        data.setOrgId(organization.getOrgId());
        data.setExtraData(organization.getExtraData());
        data.setProducts(organization.getProducts());
        data.setPostalCode(organization.getPostalCode());
        data.setActivationStatus(organization.isActive());
        data.setRcNumberVerificationStatus(organization.isRcNumberVerificationStatus());
        data.setSuperAdminEmail(getUserEmailByUserId(organization.getSuperAdminUserId()));

        Users adminUser = dataRepository.getUserByUserId(organization.getAdminUserId());
        if (adminUser != null) {
            data.setAdminEmail(adminUser.getEmail());
            data.setAdminName(adminUser.getFirstName().concat(" ").concat(adminUser.getLastName()) );
            data.setAdminPhoneNumber(adminUser.getPhoneNumber());
        }

        data.setDateCreated(utility.getFormattedStringFromDate(organization.getCreateDate()));

        return data;
    }

    private String getUserEmailByUserId(String userId) {
        if (StringUtils.isBlank(userId)) {
            return null;
        }

        Users user = dataRepository.getUserByUserId(userId);

        return user != null ? user.getEmail() : null;
    }

    private void updateOrganizationDetails(Organization organization, UpdateOrganizationRequest request) {

        organization.setName(request.getName().toUpperCase());

        if (settingsRepository.getSettingValueBooleanPrimitive(SettingEnum.ENABLE_RC_NUMBER_UPDATE)) {
            organization.setRcNumber(request.getRcNumber().toUpperCase());
            organization.setRcNumberVerificationStatus(request.isRcNumberVerified());
        }

        organization.setAddress(request.getAddress());
        organization.setCity(request.getCity());
        organization.setIndustry(request.getIndustry());
        organization.setState(request.getState());
        organization.setCountry(request.getCountry());
        organization.setPostalCode(request.getPostalCode());
        organization.setWebsite(request.getWebsite());

        if (organization.getExtraData() == null) {
            organization.setExtraData(new HashMap<>());
        }
        request.getExtraData().forEach((key, value) ->
                organization.getExtraData().put(key, value)
        );
    }

    public CreateOrganizationResponse productSignUp(OrganizationProductSignUpRequest request) {
        CreateOrganizationResponse response = new CreateOrganizationResponse();

        Organization savedOrganization = dataRepository.getOrganizationByOrgId(request.getOrgId());

        if (savedOrganization == null) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.ORGANIZATION_NOT_FOUND, context.getLocale());
        }

        if (savedOrganization.getProducts() != null && savedOrganization.getProducts().contains(request.getProductId())) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.ORGANIZATION_PRODUCT_SIGNUP, context.getLocale());
        }

        if (savedOrganization.getProducts() == null) {
            savedOrganization.setProducts(new ArrayList<>());
        }

        savedOrganization.getProducts().add(request.getProductId());

        UpdateResult updateResult = dataRepository.replaceDocumentById(DatabaseCollections.ORGANIZATIONS.getName(), Organization.class, savedOrganization, savedOrganization.getId());

        if (!updateResult.wasAcknowledged()) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.ERROR_ADDING_PRODUCT, context.getLocale());
        }

        OrganizationCreationActivityData activityData = getOrganizationCreationActivityData(Actions.PRODUCT_SIGN_UP, savedOrganization);
        auditServiceEvent.fireAsync(activityData);

        response.setOrganization(getOrganizationData(savedOrganization));
        return utility.updateResponseData(response, ResponseCode.SUCCESS, ResponseBundleCode.SUCCESS_ORGANIZATION_PRODUCT_SIGNUP, context.getLocale());

    }

    public GetOrganizationListResponse getOrganizationList(GetOrganizationListRequest request) {

        GetOrganizationListResponse response = new GetOrganizationListResponse();

        int size = request.getPageSize() != null ? request.getPageSize() : 15;
        int pageNo = request.getPageNo() != null ? request.getPageNo() : 0;
        int start = size * pageNo;

        String search = request.getSearch();

        if (StringUtils.isNotBlank(search)) {
            Users admin = dataRepository.getUserByEmail(search);
            search = admin != null ? admin.getUserId() : search;
        }

        GetOrganizationListParam param = new GetOrganizationListParam();
        param.setPageSize(size);
        param.setPageStart(start);
        param.setActive(request.getActive());
        param.setSearch(search);

        List<Organization> organizationList = dataRepository.getOrganizationList(param);

        List<OrganizationData> organizations = organizationList.stream()
                .map(this::getOrganizationData).collect(Collectors.toList());

        long totalOrganizations = dataRepository.getOrganizationCount();
        long activeOrganizations = dataRepository.getActiveOrganizationCount();

        List<OrganizationHeader> headers = getOrganizationHeaders(false);

        OrganizationMetaData metaData = new OrganizationMetaData();
        metaData.setTotal(totalOrganizations);
        metaData.setActive(activeOrganizations);
        metaData.setInactive(totalOrganizations - activeOrganizations);

        response.setMetaData(metaData);
        response.setHeaders(headers);
        response.setOrganizations(organizations);

        return utility.updateResponseData(response, ResponseCode.SUCCESS, ResponseBundleCode.SUCCESS, context.getLocale());

    }

    private List<OrganizationHeader> getOrganizationHeaders(boolean viewAll) {
        String settingValue = getOrganizationHeaderSettingValue(viewAll);

        if(StringUtils.isBlank(settingValue)) {
            return Collections.emptyList();
        }

        List<OrganizationHeader> organizationHeaders = new ArrayList<>();

        String[] values = commaPattern.split(settingValue);

        for (String val : values) {

            OrganizationHeader organizationHeader = getOrganizationHeader(val);

            if(organizationHeader != null) {
                organizationHeaders.add(organizationHeader);
            } else {
                log.warn("unknown header configured : {}", val);
            }
        }

        return organizationHeaders;
    }

    private String getOrganizationHeaderSettingValue(boolean viewAll) {
        String defaultValue = OrganizationHeaderEnum.getOrganizationHeaderEnums(viewAll).stream()
                .map(OrganizationHeaderEnum::getKey)
                .collect(Collectors.joining(","));

        SettingEnum headerSetting = viewAll ? SettingEnum.ORGANIZATION_HEADER_KEYS_ALL : SettingEnum.ORGANIZATION_HEADER_KEYS_SNAPSHOT;

        return settingsRepository.getSettingValue(headerSetting.getName(), defaultValue, headerSetting.getDescription(), null);
    }

    private OrganizationHeader getOrganizationHeader(String key) {
        OrganizationHeaderEnum organizationHeaderEnum = OrganizationHeaderEnum.fromKey(key);

        if(organizationHeaderEnum == null) {
            return null;
        }

        return new OrganizationHeader(key, organizationHeaderEnum.getLabel());
    }

    public GetOrganizationResponse findOrganization(GetOrganizationRequest request) {
        GetOrganizationResponse response = new GetOrganizationResponse();

        Organization organization;
        if (StringUtils.isNotBlank(request.getOrgId())) {
            organization = dataRepository.getOrganizationByOrgId(request.getOrgId());
        } else if (StringUtils.isNotBlank(request.getRcNumber())) {
            organization = dataRepository.getOrganizationByRcNumber(request.getRcNumber());
        } else if (StringUtils.isNotBlank(request.getName())) {
            organization = dataRepository.getOrganizationByName(request.getName());
        } else if (StringUtils.isNotBlank(request.getAdminEmail())) {
            organization = getOrganizationByAdminEmail(request.getAdminEmail());
        } else {
            organization = null;
        }

        if (organization == null) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.ORGANIZATION_NOT_FOUND, context.getLocale());
        }

        List<OrganizationHeader> headers = getOrganizationHeaders(true);

        OrganizationData organizationData = getOrganizationData(organization);
        response.setOrganization(organizationData);
        response.setHeaders(headers);
        return utility.updateResponseData(response, ResponseCode.SUCCESS, ResponseBundleCode.SUCCESS, context.getLocale());
    }

    private Organization getOrganizationByAdminEmail(String email) {

        Users user = dataRepository.getUserByEmail(email);
        if (user == null) {
            return null;
        }

        return dataRepository.getOrganizationByAdminUserId(user.getUserId());
    }

    public GetOrganizationResponse activateOrganization(ActivateOrganizationRequest request) {
        GetOrganizationResponse response = new GetOrganizationResponse();

        Organization organization = dataRepository.getOrganizationByOrgId(request.getOrgId());

        if (organization == null) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.ORGANIZATION_NOT_FOUND, context.getLocale());
        }

        if (organization.isActive()) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.ORGANIZATION_ALREADY_ACTIVE, context.getLocale());
        }

        Users admin = dataRepository.getUserByEmail(request.getAdminEmail());

        if (admin == null) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.USER_NOT_FOUND, context.getLocale());
        }

        if (!admin.isActive()) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.INACTIVE_USER, context.getLocale());
        }

        organization.setActive(true);

        UpdateResult updateResult = dataRepository.replaceDocumentById(DatabaseCollections.ORGANIZATIONS.getName(), Organization.class, organization, organization.getId());

        if (!updateResult.wasAcknowledged()) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.ERROR_UPDATING_ORGANIZATION, context.getLocale());
        }

        OrganizationActivationData activationData = new OrganizationActivationData();
        activationData.setStatus("Activate");
        activationData.setTimeStamp(new Date());
        activationData.setAdmin(admin.getEmail());

        auditActivationEvent.fireAsync(activationData);

        response.setOrganization(getOrganizationData(organization));

        return utility.updateResponseData(response, ResponseCode.SUCCESS, ResponseBundleCode.SUCCESS_ACTIVATING_ORGANIZATION, context.getLocale());
    }

    public GetOrganizationResponse deactivateOrganization(ActivateOrganizationRequest request) {
        GetOrganizationResponse response = new GetOrganizationResponse();

        Organization organization = dataRepository.getOrganizationByOrgId(request.getOrgId());

        if (organization == null) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.ORGANIZATION_NOT_FOUND, context.getLocale());
        }

        if (!organization.isActive()) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.ORGANIZATION_ALREADY_DEACTIVATED, context.getLocale());
        }

        Users admin = dataRepository.getUserByEmail(request.getAdminEmail());

        if (admin == null) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.USER_NOT_FOUND, context.getLocale());
        }

        if (!admin.isActive()) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.INACTIVE_USER, context.getLocale());
        }

        organization.setActive(false);

        UpdateResult updateResult = dataRepository.replaceDocumentById(DatabaseCollections.ORGANIZATIONS.getName(), Organization.class, organization, organization.getId());

        if (!updateResult.wasAcknowledged()) {
            return utility.updateResponseData(response, ResponseCode.ERROR, ResponseBundleCode.ERROR_UPDATING_ORGANIZATION, context.getLocale());
        }

        OrganizationActivationData activationData = new OrganizationActivationData();
        activationData.setStatus("Deactivate");
        activationData.setTimeStamp(new Date());
        activationData.setAdmin(admin.getEmail());

        auditActivationEvent.fireAsync(activationData);

        response.setOrganization(getOrganizationData(organization));

        return utility.updateResponseData(response, ResponseCode.SUCCESS, ResponseBundleCode.SUCCESS_DEACTIVATING_ORGANIZATION, context.getLocale());
    }
}
