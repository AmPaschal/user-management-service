package com.ampaschal.repositories;

import com.ampaschal.entities.GetOrganizationListParam;
import com.ampaschal.entities.GetUserListParam;
import com.ampaschal.enums.DatabaseCollections;
import com.ampaschal.mongo.EmailVerificationToken;
import com.ampaschal.mongo.Organization;
import com.ampaschal.mongo.Users;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Amusuo Paschal
 * @since 20 July 2020, 15:16:47
 */

@ApplicationScoped
public class DataRepository extends MongoDbDataService {

    public static final String USER_ID = "userId";
    public static final String EMAIL = "email";
    public static final String RC_NUMBER = "rcNumber";
    public static final String NAME = "name";
    @ConfigProperty(name = "quarkus.mongodb.database")
    private String dbName;

    @Override
    protected String getDatabaseName() {
        return dbName;
    }

    private static final String PRODUCT_ID = "productId";


    public Users getUserByIdentifier(String identifier, String idValue) {
        Bson filter = Filters.eq(identifier, idValue);

        return find(filter, getCollection(DatabaseCollections.USERS.getName(), Users.class)).first();
    }

    public Users getUserByEmail(String email) {
        Bson filter = Filters.eq(EMAIL, email.toLowerCase());

        return find(filter, getCollection(DatabaseCollections.USERS.getName(), Users.class)).first();
    }


    public Users getUserByUserId(String userId) {
        Bson filter = Filters.eq(USER_ID, userId);

        return find(filter, getCollection(DatabaseCollections.USERS.getName(), Users.class)).first();
    }

    public String getUserEmailFromUserId(String userId) {
        Bson filter = Filters.eq(USER_ID, userId);

        Document user = getCollection(DatabaseCollections.USERS.getName()).find(filter)
                .projection(Projections.include(EMAIL)).first();
        return user != null ? user.getString(EMAIL) : null;
    }

    public String getUserIdFromEmail(String email) {
        Bson filter = Filters.eq(EMAIL, email.toLowerCase());

        Document user = getCollection(DatabaseCollections.USERS.getName()).find(filter)
                .projection(Projections.include(USER_ID)).first();
        return user != null ? user.getString(USER_ID) : null;
    }

    public EmailVerificationToken getEmailVerificationToken(String token) {
        Bson filter = Filters.eq("token", token);

        return find(filter, getCollection(DatabaseCollections.EMAIL_VERIFICATION_TOKEN.getName(), EmailVerificationToken.class)).first();
    }

    public Organization getOrganizationByNameOrRCNumber(String name, String rcNumber) {
        Bson filter = Filters.or(Filters.eq(NAME, name.toUpperCase()), Filters.eq(RC_NUMBER, rcNumber.toUpperCase()));

        return find(filter, DatabaseCollections.ORGANIZATIONS.getName(), Organization.class).first();

    }

    public Organization getOrganizationByOrgId(String orgId) {
        Bson filter = Filters.eq("orgId", orgId);

        return find(filter, DatabaseCollections.ORGANIZATIONS.getName(), Organization.class).first();
    }

    public Organization getOrganizationByName(String name) {
        Bson filter = Filters.eq(NAME, name.toUpperCase());

        return find(filter, DatabaseCollections.ORGANIZATIONS.getName(), Organization.class).first();
    }

    public Organization getOrganizationByRcNumber(String rcNumber) {
        Bson filter = Filters.eq(RC_NUMBER, rcNumber.toUpperCase());

        return find(filter, DatabaseCollections.ORGANIZATIONS.getName(), Organization.class).first();
    }

    public List<Organization> getOrganizationList(GetOrganizationListParam param) {

        Bson filter = new Document();
        if (param.getActive() != null) {
            filter = Filters.and(filter, Filters.eq("active", param.getActive()));
        }
        if (StringUtils.isNotBlank(param.getSearch())) {
            filter = Filters.or(Filters.eq(NAME, param.getSearch()),
                    Filters.eq(RC_NUMBER, param.getSearch()),
                    Filters.eq("adminUserId", param.getSearch()));
        }
        FindIterable<Organization> organizations = find(filter, DatabaseCollections.ORGANIZATIONS.getName(), Organization.class)
                .sort(Sorts.descending("createDate"));

        if (param.getPageStart() != null) {
            organizations = organizations.skip(param.getPageStart());
        }

        if (param.getPageSize() != null) {
            organizations = organizations.limit(param.getPageSize());
        }

        return organizations.into(new ArrayList<>());
    }

    public long getOrganizationCount() {
        return getCollection(DatabaseCollections.ORGANIZATIONS.getName()).countDocuments();
    }

    public long getActiveOrganizationCount() {
        Bson filter = Filters.eq("active", true);
        return getCollection(DatabaseCollections.ORGANIZATIONS.getName()).countDocuments(filter);
    }

    public Organization getOrganizationByAdminUserId(String userId) {
        Bson filter = Filters.eq("adminUserId", userId);

        return find(filter, DatabaseCollections.ORGANIZATIONS.getName(), Organization.class).first();
    }

    public List<Users> getUsers(GetUserListParam param) {

        Bson filter = new Document();
        if (param.getActive() != null) {
            filter = Filters.and(filter, Filters.eq("active", param.getActive()));
        }
        if (StringUtils.isNotBlank(param.getSearch())) {
            Bson searchFilter = Filters.or(Filters.eq(NAME, param.getSearch()),
                    Filters.eq(RC_NUMBER, param.getSearch()),
                    Filters.eq("adminUserId", param.getSearch()));

            filter = Filters.and(filter, searchFilter);
        }
        FindIterable<Users> users = find(filter, DatabaseCollections.USERS.getName(), Users.class)
                .sort(Sorts.descending("createDate"));

        if (param.getPageStart() != null) {
            users = users.skip(param.getPageStart());
        }

        if (param.getPageSize() != null) {
            users = users.limit(param.getPageSize());
        }

        return users.into(new ArrayList<>());
    }
}
