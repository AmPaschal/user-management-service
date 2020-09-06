package com.ampaschal.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Amusuo Paschal C.
 * @since 8/25/2020 4:16 PM
 */

@Getter
@Setter
@NoArgsConstructor
public class OrganizationHeader implements Serializable {

    private static final long serialVersionUID = -8093753885672327791L;
    private String key;
    private String value;

    public OrganizationHeader(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
