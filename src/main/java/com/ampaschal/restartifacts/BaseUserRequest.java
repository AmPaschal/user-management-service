package com.ampaschal.restartifacts;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Amusuo Paschal C.
 * @since 7/21/2020 12:01 PM
 */

@Getter
@Setter
@ToString
public class BaseUserRequest {

    @Email
    private String email;

    private String firstName;

    @NotBlank
    private String password;

    private String phoneNumber;
    private String lastName;
    private String middleName;
    private String gender;
    private String username;
    private String title;
    private String nin;
    private String bvn;
    private String dateOfBirth;
    private List<String> productRoles;
    private List<String> globalRoles;

    private Map<String, Object> extraData = new LinkedHashMap<>();

    private String productId;

    @JsonAnySetter
    void setDetail(String key, Object value) {
        extraData.put(key, value);
    }

}
