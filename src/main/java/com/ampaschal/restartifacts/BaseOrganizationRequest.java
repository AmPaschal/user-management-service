package com.ampaschal.restartifacts;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Amusuo Paschal C.
 * @since 8/18/2020 10:10 AM
 */

@Getter
@Setter
public class BaseOrganizationRequest {

    @NotBlank
    private String name;

    @Pattern(regexp = "^[a-zA-Z0-9]{6,7}$", message = "RC Number entered should be alphanumeric and contain only 6 or 7 characters")
    private String rcNumber;

    private boolean rcNumberVerified;

    private String website;

    @NotBlank
    private String industry;

    @NotBlank
    private String address;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String country;
    private String postalCode;

    private boolean partner;


    private String productId;

    private Map<String, Object> extraData = new LinkedHashMap<>();

    @JsonAnySetter
    void setDetail(String key, Object value) {
        extraData.put(key, value);
    }

}
